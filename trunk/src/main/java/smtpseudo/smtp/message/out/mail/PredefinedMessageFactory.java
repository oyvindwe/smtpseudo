package smtpseudo.smtp.message.out.mail;

import java.util.HashMap;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.io.*;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import smtpseudo.smtp.message.exception.ValidationException;
import smtpseudo.smtp.message.MessageType;
import smtpseudo.util.template.DocumentTemplateFacotry;
import smtpseudo.util.template.DocumentTemplate;

public class PredefinedMessageFactory extends DefaultHandler{
    private static PredefinedMessageFactory instance=new PredefinedMessageFactory();
    private static Digester digester;
    private static HashMap<String, PredefinedMessage> messages=new HashMap<String,PredefinedMessage>();
    private static final String CONF_FILE="mail-template.xml";
    private static File templateDirectory;
    private static final int _1MB=1024*1024*1;
    private final static int DEFAULT_BUFF_SIZE=_1MB;
    private static int buffer=DEFAULT_BUFF_SIZE;
    protected static Collection validationExceptions = new ArrayList();
    
    private static final Log log=LogFactory.getLog(PredefinedMessageFactory.class);
    static{
       try{ 
            digester=new Digester();
            digester.setNamespaceAware(false);
            digester.setUseContextClassLoader(true);
            digester.setValidating(true);
            digester.setErrorHandler(PredefinedMessageFactory.getInstance());             
            digester.push(PredefinedMessageFactory.getInstance());
            digester.addSetProperties("mail-template",new String[]{"dir","template_buffer"},new String[]{"templateDirectory","buffer"});
            digester.addObjectCreate("mail-template/template", PredefinedMessageImpl.class);
            digester.addSetProperties("mail-template/template","x-priority","xPriority");
            MessageBodyFactory messageBodyFactory=new MessageBodyFactory();
            DocumentTemplateCreateRule documentTemplateCreateRule=new DocumentTemplateCreateRule();
            digester.addFactoryCreate("mail-template/template/text",messageBodyFactory);
            digester.addSetNext("mail-template/template/text", "setMessageBody");
            digester.addRule("mail-template/template/text/file",documentTemplateCreateRule);

            digester.addFactoryCreate("mail-template/template/html",messageBodyFactory);
            digester.addSetNext("mail-template/template/html", "setMessageBody");
            digester.addRule("mail-template/template/html/file",documentTemplateCreateRule);
            
            digester.addFactoryCreate("mail-template/template/multipart/text",messageBodyFactory);
            digester.addSetNext("mail-template/template/multipart/text", "setMessageBody");
            digester.addRule("mail-template/template/multipart/text/file",documentTemplateCreateRule);
            
            digester.addFactoryCreate("mail-template/template/multipart/html",messageBodyFactory);
            digester.addSetNext("mail-template/template/multipart/html", "setMessageBody");
            digester.addRule("mail-template/template/multipart/html/file",documentTemplateCreateRule);

            digester.addRule("mail-template/template",new CallMethodRule(1,"setPredefiendMessage",2,new Class[]{String.class, PredefinedMessageImpl.class}));
            digester.addCallParam("mail-template/template",0,"id");
            digester.addCallParam("mail-template/template",1,true);
            validationExceptions.clear(); 
            URL url = Thread.currentThread().getContextClassLoader().getResource(CONF_FILE);    
            InputSource is=new InputSource(url.toExternalForm());
            digester.parse(is);
            if (validationExceptions.size() > 0)
                throw new ValidationException(validationExceptions);
            log.debug("message template initialized. Allocatable buffer size for each template is "+buffer);
        }catch(Throwable e){
            throw new ExceptionInInitializerError(e);
        }
    }
    
    public void warning(SAXParseException e) throws SAXException {
        addValidationException(e);
    }

    public void error(SAXParseException e) throws SAXException {
        addValidationException(e);
    }

    public void fatalError(SAXParseException e) {
        addValidationException(e);
    }

    public void addValidationException(SAXException e) {
        validationExceptions.add(e);
    }
    
    private PredefinedMessageFactory() {}
  
    public static PredefinedMessageFactory getInstance(){
        return instance;
    }
    
    public static void setPredefiendMessage(String name,PredefinedMessage message){
        messages.put(name,message);
    }
    
    public static PredefinedMessage getPredefiendMessage(String name){
        return messages.get(name);
    }

    public String getTemplateDirectory() {
        return templateDirectory.toString();
    }

    public void setTemplateDirectory(String templateDirectory) {
        PredefinedMessageFactory.templateDirectory = new File(templateDirectory);
    }

    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        PredefinedMessageFactory.buffer = buffer;
    }

    private static class MessageBodyFactory extends AbstractObjectCreationFactory {
        
        public Object createObject(Attributes attributes)throws Exception{
            
            String element=getDigester().getCurrentElementName();
            MessageBody messageBody=new MessageBody(MessageType.valueOf(element.toUpperCase()));
            if(attributes!=null){
                String charset,transferEncoding;
                if((charset=attributes.getValue("charset"))!=null)
                    messageBody.setCharset(charset);
                if((transferEncoding=attributes.getValue("transfer_encoding"))!=null)
                    messageBody.setTransferEncoding(transferEncoding);
            }
            return messageBody;
        }
    }

    private static class DocumentTemplateCreateRule extends Rule {

        private DocumentTemplateFacotry factory=new DocumentTemplateFacotry(buffer);
        @Override
        public void begin(String namespace,String name,Attributes attributes) throws Exception {
            String src,encoding;
            if((src=attributes.getValue("src"))!=null && (encoding=attributes.getValue("encoding"))!=null){
                FileInputStream fin=new FileInputStream(new File(templateDirectory,src));
                MessageBody messageBody=(MessageBody)digester.peek();
                String charset=messageBody.getCharset();
                InputStream input;
                if((charset!=null && charset.length()>0) && !charset.equalsIgnoreCase(encoding))
                    input=StreamConverter.convert(fin,encoding,charset);
                else
                    input=fin;
                DocumentTemplate template=factory.createTemplate(input);
                messageBody.setTemplate(template);
            }
        }
    }
}
