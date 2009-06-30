package smtpseudo.smtp.message.in.controller.impl;

import org.apache.commons.chain.impl.ContextBase;
import smtpseudo.smtp.message.in.controller.ForwardResponse;

import javax.mail.internet.MimeMessage;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/14
 * Time: 13:49:06
 */
public class ForwardResponseContext extends ContextBase implements ForwardResponse {
    private MimeMessage toBeforwarded;
    private Exception exception;
    
    public ForwardResponseContext(MimeMessage toBeforwarded){
        this.toBeforwarded=toBeforwarded;
    }
    
    public MimeMessage getForwardMessage() {
        return toBeforwarded;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
