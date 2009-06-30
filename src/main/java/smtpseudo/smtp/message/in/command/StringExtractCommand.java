package smtpseudo.smtp.message.in.command;

import java.io.InputStream;
import java.io.IOException;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/07
 * Time: 22:49:16
 */
public interface StringExtractCommand {
    StringMatchResult execute(InputStream in) throws IOException;
}
