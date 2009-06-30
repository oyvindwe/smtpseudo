package smtpseudo.smtp.message.in.mail;

import smtpseudo.smtp.message.MessageType;
import java.io.InputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/17
 * Time: 17:00:52
 * To change this template use File | Settings | File Templates.
 */
public interface MimeMessageAnalyzer {
    MimeMessageHeader getHeader(int depth);
    InputStream getInputStream(MessageType type,int depth) throws IOException;
}
