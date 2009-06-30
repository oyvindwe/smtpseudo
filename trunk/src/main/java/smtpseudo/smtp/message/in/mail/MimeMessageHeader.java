package smtpseudo.smtp.message.in.mail;

import javax.mail.Address;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/17
 * Time: 17:16:16
 * To change this template use File | Settings | File Templates.
 */
public interface MimeMessageHeader {
    public List<Address> getAddressFrom();
    public List<Address> getAddressTo();
    public List<Address> getAddressCC();
    public String getSubject();
}
