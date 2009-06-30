package smtpseudo.smtp.message.out.mail;

import smtpseudo.util.template.DocumentTemplate;
import smtpseudo.smtp.message.in.command.StringMatchResult;
import smtpseudo.smtp.message.MessageType;
import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageBody {
    private final MessageType type;
    private String charset;
    private String transferEncoding;
    private DocumentTemplate template;
    private static final Log log= LogFactory.getLog(MessageBody.class);

    public MessageBody(MessageType type,String charset,String transferEncoding){
        if(type==null||charset==null||transferEncoding==null)
            throw new NullPointerException();
        this.type=type;
        this.charset=charset;
        this.transferEncoding=transferEncoding;
    }

    public MessageBody(MessageType type){
        if(type==null)
            throw new NullPointerException();
        this.type=type;
    }

    public synchronized InputStream getInputStream(StringMatchResult stringMatchResult) throws IOException {
        if(getTemplate() ==null)
            throw new NullPointerException();
        for(String keyToLookFor: getTemplate().getVariables()){
            log.info("keyToLookFor:"+keyToLookFor);
            String toBeInserted=stringMatchResult.get(keyToLookFor);
            log.info("toBeInserted:"+toBeInserted);
            if(toBeInserted!=null)
                getTemplate().setVariable(keyToLookFor,toBeInserted.getBytes(charset));
        }
        return getTemplate().getInputStream();
    }

    public synchronized InputStream getInputStream(Map<String,String> variables) throws IOException {
        if(getTemplate() ==null)
            throw new NullPointerException();
        for(String keyToLookFor: getTemplate().getVariables()){
            log.info("keyToLookFor:"+keyToLookFor);
            String toBeInserted=variables.get(keyToLookFor);
            log.info("toBeInserted:"+toBeInserted);
            if(toBeInserted!=null)
                getTemplate().setVariable(keyToLookFor,toBeInserted.getBytes(charset));
        }
        return getTemplate().getInputStream();
    }

    public synchronized InputStream getInputStream() throws IOException {
        if(getTemplate() ==null)
            throw new NullPointerException();
        return getTemplate().getInputStream();
    }
    
    public MessageType getMessageType(){
        return type;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getTransferEncoding() {
        return transferEncoding;
    }

    public void setTransferEncoding(String transferEncoding) {
        this.transferEncoding = transferEncoding;
    }

    public DocumentTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DocumentTemplate template) {
        this.template = template;
    }
}
