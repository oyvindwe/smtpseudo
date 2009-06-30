package smtpseudo.smtp;

import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import javax.mail.Session;
import java.io.ByteArrayInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/12
 * Time: 9:42:51
 * To change this template use File | Settings | File Templates.
 */
public class Mail {
    private byte[] messageData;
	private String envelopeSender;
	private String envelopeReceiver;
    private ByteArrayInputStream stream;

    public Mail(String envelopeSender, String envelopeReceiver,byte[] messageData) { 
        this.envelopeSender = envelopeSender;
        this.envelopeReceiver = envelopeReceiver;
        this.messageData = messageData;
        this.stream=new ByteArrayInputStream(messageData);
    }

    public MimeMessage getMimeMessage(Session session) throws MessagingException{
        resetStream();
		return new MimeMessage(session,stream);
	}

    public byte[] getMessageData() {
        return messageData;
    }

    public String getEnvelopeSender() {
        return envelopeSender;
    }

    public String getEnvelopeReceiver() {
        return envelopeReceiver;
    }

    public void resetStream(){
        stream.reset();
    }
}
