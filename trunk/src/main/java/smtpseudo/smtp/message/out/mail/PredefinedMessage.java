package smtpseudo.smtp.message.out.mail;

import smtpseudo.smtp.message.MessageType;

public interface PredefinedMessage {

    String getCharset();

    MessageBody getMessageBody(MessageType type);

    String getTitle();

    int getxPriority();

    boolean hasMessageType(MessageType type);

    void setCharset(String charset);

    void setMessageBody(MessageBody messageBody);

    void setTitle(String title);

    void setxPriority(int xPriority);

}
