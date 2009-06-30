package smtpseudo.smtp.message.in.controller.impl;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smtpseudo.smtp.message.in.controller.ForwardRequest;
import smtpseudo.smtp.message.out.mail.TemplateMessageBuilder;
import smtpseudo.smtp.message.out.mail.MimeMessageComposer;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import javax.mail.MessagingException;
import javax.mail.Message;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.common.resources.Resources;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/14
 * Time: 12:45:30
 */
public abstract class Forwarder implements Command {
    private static SqlMapClient sqlMap;
    private String mailFrom;
    protected List<String> rcptTO=new ArrayList<String>();
    protected List<String> rcptCC=new ArrayList<String>();
    protected List<String> rcptBCC=new ArrayList<String>();
    protected String lookup;
    protected TemplateMessageBuilder messageBuilder;
    private static final Log log= LogFactory.getLog(Forwarder.class);
    static{
        Reader reader;
        try {
            reader=Resources.getResourceAsReader("conf/SqlMapConfig.xml");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
    }
    public boolean execute(Context context) throws Exception {
        return canForward((ForwardRequest)context);
    }

    public boolean canForward(ForwardRequest request) throws Exception {
        MimeMessage message=forward(request);
        if(message!=null){
            ForwardResponseContext response=new ForwardResponseContext(message);
            setRecipients(response);
            setLookupRecipient(response,request.getEnvelopeReceiver());
            message.setFrom(new InternetAddress(mailFrom));
            message.saveChanges();
            request.setResponse(response);
            return true;
        }else
            return false;
    }

    protected void setRecipients(ForwardResponseContext response) throws MessagingException {
        MimeMessage message=response.getForwardMessage();
        if(rcptTO.size()>0)
            setRecipients(message,rcptTO,Message.RecipientType.TO);
        if(rcptCC.size()>0)
            setRecipients(message,rcptCC,Message.RecipientType.CC);
        if(rcptBCC.size()>0)
            setRecipients(message,rcptBCC,Message.RecipientType.BCC);
    }

    protected void setLookupRecipient(ForwardResponseContext response,String originalRcptTo) throws MessagingException {
        if(lookup!=null){
            MimeMessage message=response.getForwardMessage();
            IcbuAccountCondition condition = new IcbuAccountCondition(originalRcptTo,lookup);
            String lookedupAddress=null;
            try{
                lookedupAddress=(String)sqlMap.queryForObject("selectEmailAddress", condition);
            }catch(SQLException e){
                try{
                    message.addRecipients(Message.RecipientType.TO,originalRcptTo);
                }catch(AddressException originalRcptToInvalid){
                    log.error(originalRcptToInvalid.getRef() +" is not allowed email address.");
                }
                response.setException(e);
            }
            if(lookedupAddress!=null){
                try{
                    message.addRecipients(Message.RecipientType.TO,lookedupAddress);
                }catch(AddressException lookedupAddressInvalid){
                      log.error(lookedupAddressInvalid.getRef() +" is not allowed email address.");
                }
            }
        }
    }
    
    protected MimeMessage rebuildMessage(MimeMessage messsage) throws IOException, MessagingException {
        return MimeMessageComposer.rebuild(messsage);
    }

    private static void setRecipients(MimeMessage message,List<String> address, Message.RecipientType type) throws MessagingException {
        for(String addressRcptTo:address)
            try{
                message.addRecipients(type,addressRcptTo);
            }catch(AddressException e){
                 log.error(e.getRef() +" is not allowed email address.");
            }
    }
    
    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public void addToAddress(String address){
        this.rcptTO.add(address);
    }

    public void addCcAddress(String address){
        this.rcptCC.add(address);
    }

    public void addBccAddress(String address){
        this.rcptBCC.add(address);
    }

    public abstract MimeMessage forward(ForwardRequest request) throws IOException, MessagingException;

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup=lookup;
    }

    public TemplateMessageBuilder getMessageBuilder() {
        return messageBuilder;
    }

    public void setMessageBuilder(TemplateMessageBuilder messageBuilder) {
        this.messageBuilder=messageBuilder;
    }

    public String toString(){
        return new ToStringBuilder(this)
                .append("mailFrom :",mailFrom)
                .append("rcptTO :",rcptTO)
                .append("rcptCC :",rcptCC)
                .append("rcptBCC :",rcptBCC)
                .append("lookup :", lookup)
                .append("messageBuilder :", messageBuilder)
                .toString();
    }
}
