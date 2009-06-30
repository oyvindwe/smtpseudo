package smtpseudo.smtp.message.out.mail;

import smtpseudo.smtp.message.MessageType;
import java.util.EnumMap;
import java.util.Map;

public class PredefinedMessageImpl implements PredefinedMessage {
    private String title="";
    private final String DEFAULT_CHARSET="ISO-8859-1";
    private String charset=DEFAULT_CHARSET;
    private int xPriority=3;
    private Map<MessageType, MessageBody> messages=new EnumMap<MessageType,MessageBody>(MessageType.class);
    private int messageType;
    
    public PredefinedMessageImpl() {
    }
    
    public void setTitle(String title){
        this.title=title;
    }
    
    public String getTitle(){
        return title;
    }
    
    public boolean hasMessageType(MessageType type){
        return (messageType & type.value())==type.value();
    }
    
    public MessageBody getMessageBody(MessageType type) {
        if((messageType & type.value())==0)
            throw new IllegalArgumentException("MessageBody for " + type.toString() +" not found.");
        return messages.get(type);
    }

    public void setMessageBody(MessageBody messageBody) {
        MessageType type=messageBody.getMessageType();
        messageType|=type.value();
        if(messageBody.getCharset()==null)
            messageBody.setCharset(charset);
        messages.put(type, messageBody);
    }
    
    public void setCharset(String charset){
        this.charset=charset;
    }
    
    public String getCharset(){
        return this.charset;
    }
    
    public void setxPriority(int xPriority){
        this.xPriority=xPriority;
    }
    
    public int getxPriority(){
        return this.xPriority;
    }
}
