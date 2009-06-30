package smtpseudo.smtp.message.in.controller;

import org.apache.commons.chain.Context;
import javax.mail.internet.MimeMessage;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/14
 * Time: 13:45:49
 */
public interface ForwardResponse extends Context {
    MimeMessage getForwardMessage();
    Exception getException();
}
