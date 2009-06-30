package smtpseudo.smtp.message.in.controller.impl;

import org.apache.commons.chain.impl.ContextBase;
import smtpseudo.smtp.message.in.controller.ForwardRequest;
import smtpseudo.smtp.message.in.controller.ForwardResponse;
import smtpseudo.smtp.message.in.mail.MimeMessageAnalyzer;
import smtpseudo.smtp.Mail;
import smtpseudo.smtp.message.in.mail.MimeMessageAnalyzerFactory;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.MessagingException;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/14
 * Time: 11:53:54
 */
public class ForwardRequestContext extends ContextBase implements ForwardRequest {
    private ForwardResponse response;
    private MimeMessageAnalyzer analyzer;
    private final Mail mail;
    private final Session session;

    public ForwardRequestContext(Session session,Mail mail) throws Exception {
        this.session=session;
        this.mail=mail;
        this.analyzer=MimeMessageAnalyzerFactory.create(mail.getMimeMessage(null));
    }

    public Session getSession() {
        return session;
    }

    public ForwardResponse getResponse() {
        return response;
    }

    public void setResponse(ForwardResponse response) {
        this.response=response;
    }

    public String getEnvelopeSender() {
        return mail.getEnvelopeSender();
    }

    public String getEnvelopeReceiver() {
        return mail.getEnvelopeReceiver();
    }

    public MimeMessage getMessageReceived() throws MessagingException {
        return mail.getMimeMessage(session);
    }

    public MimeMessageAnalyzer getAnalyzer(){
        mail.resetStream();
        return analyzer;
    }
}
