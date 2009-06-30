package smtpseudo.smtp.message.in.controller.impl.forwarder;

import smtpseudo.smtp.message.in.controller.impl.Forwarder;
import smtpseudo.smtp.message.in.controller.ForwardRequest;
import smtpseudo.smtp.message.in.mail.MimeMessageHeader;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import javax.mail.Address;
import java.util.List;
import java.io.IOException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/15
 * Time: 13:22:40
 * To change this template use File | Settings | File Templates.
 */
public class FromAddressForwarder extends Forwarder {
    private String[] ifFrom;
    private static final Log log= LogFactory.getLog(FromAddressForwarder.class);
    @Override
    public MimeMessage forward(ForwardRequest request) throws MessagingException, IOException {
        if(ifFromAddress(request)){
            log.debug("ifFrom:"+ifFrom);
            if(messageBuilder!=null)
                return messageBuilder.build(request.getSession(),request.getAnalyzer());
            else
                return rebuildMessage(request.getMessageReceived());
        }
        return null;
    }

    public void setIfFrom(String[] ifFrom) {
        this.ifFrom = ifFrom;
    }

    private boolean ifFromAddress(ForwardRequest request){
        if(ifFrom==null)
            return true;//any sender
        String envelopSender=request.getEnvelopeSender();
        for(String addressIfFrom:ifFrom){
            log.debug("[address to match:"+addressIfFrom + ",envelop sender:"+envelopSender+"]");
            if(addressIfFrom.equalsIgnoreCase(envelopSender)||envelopSender.contains(addressIfFrom))
                return true;

        }
        MimeMessageHeader header=request.getAnalyzer().getHeader(0);
        if(header!=null){
            List<Address> addressFoundInHeader=header.getAddressFrom();
            if(addressFoundInHeader!=null)
                for(String addressIfFrom:ifFrom)
                    for(Address addressFound:addressFoundInHeader){
                        log.debug("[address to match:"+addressIfFrom +",address found in body:"+addressFound+"]");
                        if(addressFound.toString().contains(addressIfFrom)||addressIfFrom.equalsIgnoreCase(addressFound.toString()))
                            return true;
                    }

        }
        return false;
    }

    public String toString(){
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("From Address to match :", ifFrom)
                .toString();
    }
}
