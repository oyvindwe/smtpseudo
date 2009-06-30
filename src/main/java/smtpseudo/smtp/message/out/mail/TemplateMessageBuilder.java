package smtpseudo.smtp.message.out.mail;

import smtpseudo.smtp.message.in.command.StringExtractCommandFactory;
import smtpseudo.smtp.message.in.command.StringExtractCommand;
import smtpseudo.smtp.message.in.command.StringMatchResult;
import smtpseudo.smtp.message.in.mail.MimeMessageAnalyzer;
import smtpseudo.smtp.message.MessageType;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/15
 * Time: 15:26:29
 * To change this template use File | Settings | File Templates.
 */
public class TemplateMessageBuilder {
    private PredefinedMessage pmsg;
    private StringExtractCommand cmdOnText;
    private StringExtractCommand cmdOnHtml;
    private static final Log log= LogFactory.getLog(TemplateMessageBuilder.class);

    public MimeMessage build(Session session, MimeMessageAnalyzer analyzer) throws IOException, MessagingException { 
        InputStream message;
        StringMatchResult resultText=null;
        StringMatchResult resultHtml=null;
        if(pmsg!=null){
            if(cmdOnText != null && (message=analyzer.getInputStream(MessageType.TEXT, 0))!=null){
                log.debug("stripping cmdOnText on "+ MessageType.TEXT.getContentType());
                resultText=cmdOnText.execute(message);
                log.debug(resultText.toString());
            }
            if(cmdOnHtml != null && (message=analyzer.getInputStream(MessageType.HTML, 0))!=null){
                log.debug("stripping cmdOnHtml on "+ MessageType.HTML.getContentType());
                resultHtml=cmdOnHtml.execute(message);
                log.debug(resultHtml.toString());
            }
            if(resultText!=null && resultHtml!=null){
                log.debug("stripped:"+resultText +MessageType.TEXT.getContentType());
                log.debug("stripped:"+resultHtml +MessageType.HTML.getContentType());
                return MimeMessageComposer.compose(session,pmsg,resultText,resultHtml);
            }
            if(resultText!=null){
                log.debug("stripped:"+resultText +" "+MessageType.TEXT.getContentType());
                return MimeMessageComposer.compose(session,pmsg,resultText);
            }

            if(resultHtml!=null){
                log.debug("stripped:"+resultHtml +" "+MessageType.HTML.getContentType());
                return MimeMessageComposer.compose(session,pmsg,resultHtml);
            }
            return MimeMessageComposer.compose(session,pmsg);
        }
        return null;
    }

    public void setTemplate(String template) {
        pmsg=PredefinedMessageFactory.getPredefiendMessage(template);
        if(pmsg==null)
            throw new NullPointerException("no matching template found:"+template);
    }

    public void setCmdOnText(String cmdOnText) {
        this.cmdOnText=StringExtractCommandFactory.get(cmdOnText);
    }

    public void setCmdOnHtml(String cmdOnHtml) {
        this.cmdOnHtml=StringExtractCommandFactory.get(cmdOnHtml);
    }
}
