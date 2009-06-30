package smtpseudo.smtp.message.out.mail;

import javax.activation.DataSource;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/11
 * Time: 19:33:53
 */
public class MimeMessageDataSource implements DataSource {
    private InputStream inputStream;
    private String contentType;

    public MimeMessageDataSource(InputStream in,String contentType){
        this.inputStream=in;
        this.contentType=contentType;
    }

    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    public OutputStream getOutputStream() throws IOException {
        return null;// not implemented
    }

    public String getContentType() {
        return contentType;
    }

    public String getName() {
        return "";
    }
}
