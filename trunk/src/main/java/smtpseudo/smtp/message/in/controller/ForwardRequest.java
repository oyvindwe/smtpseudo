package smtpseudo.smtp.message.in.controller;

import org.apache.commons.chain.Context;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.MessagingException;
import smtpseudo.smtp.message.in.mail.MimeMessageAnalyzer;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/14
 * Time: 11:23:04
 */
public interface ForwardRequest extends Context {
    Session getSession();
    ForwardResponse getResponse();
    void setResponse(ForwardResponse response);
    String getEnvelopeSender();
    String getEnvelopeReceiver();
    MimeMessage getMessageReceived() throws MessagingException;
    MimeMessageAnalyzer getAnalyzer();
}
