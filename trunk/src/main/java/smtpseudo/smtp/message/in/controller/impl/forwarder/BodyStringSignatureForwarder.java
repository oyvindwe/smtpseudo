package smtpseudo.smtp.message.in.controller.impl.forwarder;

import smtpseudo.smtp.message.in.controller.impl.Forwarder;
import smtpseudo.smtp.message.in.controller.ForwardRequest;
import smtpseudo.smtp.message.in.mail.MimeMessageAnalyzer;
import smtpseudo.smtp.message.MessageType;
import smtpseudo.util.matcher.ByteArrayExtractor;
import smtpseudo.util.matcher.ByteArrayExtractorFactory;
import smtpseudo.util.matcher.BoyerMooreByteArrayMatcher;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/18
 * Time: 13:16:08
 * To change this template use File | Settings | File Templates.
 */
public class BodyStringSignatureForwarder extends Forwarder {
    private BoyerMooreByteArrayMatcher ifInText;
    private BoyerMooreByteArrayMatcher ifInHtml;
    private static final Log log= LogFactory.getLog(BodyStringSignatureForwarder.class);
    @Override
    public MimeMessage forward(ForwardRequest request) throws IOException, MessagingException {
        MimeMessageAnalyzer analyzer=request.getAnalyzer();
        log.debug("ifInText["+ifInText+"],ifInHtml["+ifInHtml+"]");
        if(ifInText!=null&&ifInHtml!=null){
            InputStream isText=analyzer.getInputStream(MessageType.TEXT,0);
            InputStream isHtml=analyzer.getInputStream(MessageType.HTML,0);
            if(isStringInText(isText,ifInText) && isStringInText(isHtml,ifInHtml))
                return getMimeMessageToReturn(request);
           
        }else if(ifInText!=null){
            InputStream isText=analyzer.getInputStream(MessageType.TEXT,0);
            if(isStringInText(isText,ifInText))
                return getMimeMessageToReturn(request);
        }else if(ifInHtml!=null){
            InputStream isHtml=analyzer.getInputStream(MessageType.HTML,0);
            if(isStringInText(isHtml,ifInHtml))
                return getMimeMessageToReturn(request);
        }
        return null;
    }

    private MimeMessage getMimeMessageToReturn(ForwardRequest request) throws MessagingException, IOException {
        if(messageBuilder!=null)
            return messageBuilder.build(request.getSession(),request.getAnalyzer());
        else
            return rebuildMessage(request.getMessageReceived());
    }

    private static boolean isStringInText(InputStream is,BoyerMooreByteArrayMatcher matcher) throws IOException {
        if(is!=null){
            ByteArrayExtractor extractor= ByteArrayExtractorFactory.getByteArrayExtractor();
            return extractor.indexOf(is,matcher)>0;
        }else
            return false;
    }

    public void setIfInText(BoyerMooreByteArrayMatcher ifInText) {
        this.ifInText = ifInText;
    }

    public void setIfInHtml(BoyerMooreByteArrayMatcher ifInHtml) {
        this.ifInHtml = ifInHtml;
    }
}
