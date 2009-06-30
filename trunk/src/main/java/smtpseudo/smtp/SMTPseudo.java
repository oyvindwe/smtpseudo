package smtpseudo.smtp;

import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import java.io.*;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import smtpseudo.smtp.message.in.controller.ForwardRequest;
import smtpseudo.smtp.message.in.controller.ForwardResponse;
import smtpseudo.smtp.message.in.controller.Controller;
import smtpseudo.smtp.message.in.controller.ControllerInitializer;
import smtpseudo.smtp.message.in.controller.impl.ForwardRequestContext;
import smtpseudo.smtp.message.in.mail.MimeMessageAnalyzerFactory;
import smtpseudo.smtp.message.in.mail.MimeMessageAnalyzer;
import smtpseudo.smtp.message.in.mail.MimeMessageHeader;
import smtpseudo.smtp.message.out.mail.MimeMessageComposer;
import smtpseudo.smtp.message.transport.RetryStrategy;
import smtpseudo.smtp.message.transport.AdditiveWaitRetryStrategy;
import smtpseudo.smtp.message.exception.RetryException;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/12
 * Time: 9:49:17
 * To change this template use File | Settings | File Templates.
 */
public class SMTPseudo implements SimpleMessageListener{
	private final static Log log=LogFactory.getLog(SMTPseudo.class);
    Session session;
	SMTPServer server;
    private String mydomain;
    private String[] allowedRemote;
	List<Mail> messages=Collections.synchronizedList(new ArrayList<Mail>());
    private Controller controller= ControllerInitializer.getController();
    private ExecutorService service;
    private RetryStrategy retryStrategy;
    private File errDump;
    
	public SMTPseudo(int port,int forwarders,Session session,
                     String mydomain,String[] allowedRemote,File errDump){
        super();
		this.server = new SMTPServer(new SimpleMessageListenerAdapter(this));
		this.server.setPort(port);
        this.session=session;
        this.service=Executors.newFixedThreadPool(forwarders);
        this.mydomain=mydomain;
        this.allowedRemote=allowedRemote;
        this.retryStrategy=new AdditiveWaitRetryStrategy();
        this.errDump=errDump;
	}

	public void setPort(int port){
		this.server.setPort(port);
	}

	public void setHostname(String hostname){
		this.server.setHostName(hostname);
	}

	public void start(){
		this.server.start();
	}

	public void stop(){
		this.server.stop();
	}

	public boolean accept(String from, String recipient){
        for(String remotedomain:allowedRemote){
            if(from.endsWith(remotedomain) && recipient.endsWith(mydomain))
                return true;
        }
        log.info("rejected mail from:"+from+" to:"+recipient);
        return false;
	}

	public void deliver(String from, String recipient, InputStream data) throws IOException {
        log.info("forwarding message from:"+from+" to:"+recipient);
        int current;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
		data=new BufferedInputStream(data);
        byte[] tmp=new byte[1024];
        while((current=data.read(tmp,0,tmp.length))!=-1)
            out.write(tmp,0,current);
        data.close();
        Mail received=new Mail(from,recipient,out.toByteArray());
        service.execute(new ForwardMailCommand(received));
	}

	protected Session getSession(){
		return Session.getDefaultInstance(new Properties());
	}

	public List<Mail> getMessages(){
		return this.messages;
	}

	public SMTPServer getServer(){
		return this.server;
	}

    private class ForwardMailCommand implements Runnable{
        Mail mail;
        ForwardResponse response;
        MimeMessage message;
        
        public ForwardMailCommand(Mail mail){
            this.mail=mail;
        }

        public void run() {

            while(retryStrategy.shouldRetry())
                try{
                    if(message==null){
                        log.info(Thread.currentThread()+" creating message to be forwarded");
                        ForwardRequest request=new ForwardRequestContext(session,mail);
                        controller.process(request);
                        response=request.getResponse();
                        if(response!=null){
                            Exception e;
                            if((e=response.getException())!=null)
                                throw e;
                            else
                                message=response.getForwardMessage();
                        }else{
                            MimeMessage tmp=mail.getMimeMessage(session);
                            MimeMessageAnalyzer analyzer=MimeMessageAnalyzerFactory.create(tmp);
                            MimeMessageHeader header=analyzer.getHeader(0);
                            List<Address> bodyFrom;
                            try{
                                if(header==null||(bodyFrom=header.getAddressFrom())==null||bodyFrom.size()==0){
                                    message=MimeMessageComposer.rebuild(tmp);
                                    message.setFrom(new InternetAddress(mail.getEnvelopeSender()));
                                }else{
                                    message=MimeMessageComposer.rebuild(tmp);
                                    Address[] address=bodyFrom.toArray(new Address[]{});
                                    message.addFrom(address);
                                }
                            }catch(AddressException e){
                                log.info(e.getRef() +" is not a valid email address");
                            }
                            message.setRecipient(Message.RecipientType.TO,new InternetAddress(controller.getDumpster()));
                        }
                        if(message!=null)
                            message.saveChanges();
                    }
                    Transport.send(message);
                    for(Address address :message.getAllRecipients())
                        log.info(message.getMessageID()+ " forwarded to "+ address);
                    break;
                } catch (Exception e) {
                    try {
                        log.error(e.getMessage());
                        System.out.println("Retrying...");
                        retryStrategy.tryRetry();
                    } catch (RetryException retryExp) {
                        try {
                            MimeMessage failedToSend=response.getForwardMessage();
                            String file=failedToSend.getMessageID()
                                    .substring(1,failedToSend.getMessageID().lastIndexOf(">"))+".msg";
                            log.error("message flushed to "+file);
                            int at=mail.getEnvelopeReceiver().lastIndexOf('@');
                            File recipientDump;
                            if(at>0)
                                recipientDump=new File(errDump,mail.getEnvelopeReceiver().substring(0,at));
                            else
                                recipientDump=new File(errDump,mail.getEnvelopeReceiver());
                            if(!recipientDump.exists())
                                recipientDump.mkdir();
                            failedToSend.writeTo(new FileOutputStream(new File(recipientDump,file)));
                        } catch (Exception fatal) {
                            fatal.printStackTrace();
                        }
                        System.out.println(retryExp.getMessage());
                        System.out.println("fatal error occured.");
                        System.exit(1);
                    }
                }
        }
    }

    public void halt() {
        System.out.println("shutting down...");
        service.shutdown();
        server.stop();
        System.out.println("shutdown.");
    }
    
    public Log getLogger(){
        return log;
    }
}