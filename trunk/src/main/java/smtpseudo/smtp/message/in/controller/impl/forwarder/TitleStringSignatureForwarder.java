package smtpseudo.smtp.message.in.controller.impl.forwarder;

import smtpseudo.smtp.message.in.controller.impl.Forwarder;
import smtpseudo.smtp.message.in.controller.ForwardRequest;
import smtpseudo.smtp.message.in.mail.MimeMessageAnalyzer;

import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import java.io.IOException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/18
 * Time: 14:52:19
 * To change this template use File | Settings | File Templates.
 */
public class TitleStringSignatureForwarder extends Forwarder {
    private String ifInTitle;
     private static final Log log= LogFactory.getLog(TitleStringSignatureForwarder.class);
    @Override
    public MimeMessage forward(ForwardRequest request) throws IOException, MessagingException {
        MimeMessageAnalyzer analyzer=request.getAnalyzer();
        String subject=analyzer.getHeader(0).getSubject();
        if(subject!=null){
            if(subject.contains(ifInTitle)){
                log.info("ifInTitle:"+ifInTitle);
                if(messageBuilder!=null)
                    return messageBuilder.build(request.getSession(),request.getAnalyzer());
                else
                    return rebuildMessage(request.getMessageReceived());
            }
        }
        return null;
    }

    public void setIfInTitle(String ifInTitle) {
        this.ifInTitle = ifInTitle;
    }

    public String toString(){
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("Title Search String :", ifInTitle)
                .toString();
    }
}
