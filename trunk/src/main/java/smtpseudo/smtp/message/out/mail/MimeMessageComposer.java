package smtpseudo.smtp.message.out.mail;

import smtpseudo.smtp.message.in.command.StringMatchResult;
import smtpseudo.smtp.message.MessageType;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/11
 * Time: 17:24:11
 * To change this template use File | Settings | File Templates.
 */
public class MimeMessageComposer {

    public static MimeMessage rebuild(MimeMessage message) throws IOException, MessagingException {
        Enumeration e=message.getNonMatchingHeaders(new String[]{"Subject","Content-Type","Content-Transfer-Encoding"});
        while(e.hasMoreElements()){
            Header header=(Header)e.nextElement();
            message.removeHeader(header.getName());
        }
        return message;
    }

    static MimeMessage compose(Session session,PredefinedMessage pmsg,StringMatchResult text,
                                      StringMatchResult html) throws MessagingException, IOException {
        MimeMessage mimeMessage=new MimeMessage(session);
        if(pmsg.hasMessageType(MessageType.TEXT) && pmsg.hasMessageType(MessageType.HTML)){
            MessageBody bodyText=pmsg.getMessageBody(MessageType.TEXT);
            MessageBody bodyHtml=pmsg.getMessageBody(MessageType.HTML);
            InputStream inputText=bodyText.getInputStream(text);
            InputStream inputHtml=bodyHtml.getInputStream(html);
            DataSource messageText=new MimeMessageDataSource(inputText,
                    MessageType.TEXT.getContentType()+"; charset=" + bodyText.getCharset());
            DataSource messageHtml=new MimeMessageDataSource(inputHtml,
                    MessageType.HTML.getContentType()+"; charset=" + bodyHtml.getCharset());
            Multipart mpAlternative=new MimeMultipart("alternative");
            BodyPart bodyPartText=getBodyPart(messageText);
            BodyPart bodyPartHtml=getBodyPart(messageHtml);
            bodyPartText.setHeader("Content-Transfer-Encoding",bodyText.getTransferEncoding());
            bodyPartHtml.setHeader("Content-Transfer-Encoding",bodyHtml.getTransferEncoding());
            mpAlternative.addBodyPart(bodyPartText);
            mpAlternative.addBodyPart(bodyPartHtml);
            mimeMessage.setContent(mpAlternative);
        }
        int priority=pmsg.getxPriority();
        if(priority>0 || 6>priority)
            mimeMessage.setHeader("X-Priority",String.valueOf(priority));
        mimeMessage.setSubject(pmsg.getTitle(),pmsg.getCharset());
        return mimeMessage;
    }

    static MimeMessage compose(Session session, PredefinedMessage pmsg,StringMatchResult result) throws IOException, MessagingException {
        MimeMessage mimeMessage=new MimeMessage(session);
        if(pmsg.hasMessageType(MessageType.TEXT) && pmsg.hasMessageType(MessageType.HTML)){
            MessageBody bodyText=pmsg.getMessageBody(MessageType.TEXT);
            MessageBody bodyHtml=pmsg.getMessageBody(MessageType.HTML);
            InputStream inputText=bodyText.getInputStream(result);
            InputStream inputHtml=bodyHtml.getInputStream(result);
            DataSource messageText=new MimeMessageDataSource(inputText,
                    MessageType.TEXT.getContentType()+"; charset=" + bodyText.getCharset());
            DataSource messageHtml=new MimeMessageDataSource(inputHtml,
                    MessageType.HTML.getContentType()+"; charset=" + bodyHtml.getCharset());
            Multipart mpAlternative=new MimeMultipart("alternative");
            BodyPart bodyPartText=getBodyPart(messageText);
            BodyPart bodyPartHtml=getBodyPart(messageHtml);
            bodyPartText.setHeader("Content-Transfer-Encoding",bodyText.getTransferEncoding());
            bodyPartHtml.setHeader("Content-Transfer-Encoding",bodyHtml.getTransferEncoding());
            mpAlternative.addBodyPart(bodyPartText);
            mpAlternative.addBodyPart(bodyPartHtml);
            mimeMessage.setContent(mpAlternative);

        }else if(pmsg.hasMessageType(MessageType.TEXT)){
            MessageBody bodyText=pmsg.getMessageBody(MessageType.TEXT);
            InputStream inputText=bodyText.getInputStream(result);
            DataSource messageText=new MimeMessageDataSource(inputText,
                    MessageType.TEXT.getContentType()+"; charset=" + bodyText.getCharset());
            mimeMessage.setDataHandler(new DataHandler(messageText));
            mimeMessage.setHeader("Content-Transfer-Encoding",bodyText.getTransferEncoding());

        }else if(pmsg.hasMessageType(MessageType.HTML)){
            MessageBody bodyHtml=pmsg.getMessageBody(MessageType.HTML);
            InputStream inputHtml=bodyHtml.getInputStream(result);
            DataSource messageHtml=new MimeMessageDataSource(inputHtml,
                    MessageType.HTML.getContentType()+"; charset="+bodyHtml.getCharset());
            mimeMessage.setDataHandler(new DataHandler(messageHtml));
            mimeMessage.setHeader("Content-Transfer-Encoding",bodyHtml.getTransferEncoding());
        }
        int priority=pmsg.getxPriority();
        if(priority>0 || 6>priority)
            mimeMessage.setHeader("X-Priority",String.valueOf(priority));
        mimeMessage.setSubject(pmsg.getTitle(),pmsg.getCharset());
        return mimeMessage;
    }

    static MimeMessage compose(Session session, PredefinedMessage pmsg) throws IOException, MessagingException {
        MimeMessage mimeMessage=new MimeMessage(session);
        if(pmsg.hasMessageType(MessageType.TEXT) && pmsg.hasMessageType(MessageType.HTML)){
            MessageBody bodyText=pmsg.getMessageBody(MessageType.TEXT);
            MessageBody bodyHtml=pmsg.getMessageBody(MessageType.HTML);
            InputStream inputText=bodyText.getInputStream();
            InputStream inputHtml=bodyHtml.getInputStream();
            DataSource messageText=new MimeMessageDataSource(inputText,
                    MessageType.TEXT.getContentType()+"; charset=" + bodyText.getCharset());
            DataSource messageHtml=new MimeMessageDataSource(inputHtml,
                    MessageType.HTML.getContentType()+"; charset=" + bodyHtml.getCharset());
            Multipart mpAlternative=new MimeMultipart("alternative");
            BodyPart bodyPartText=getBodyPart(messageText);
            BodyPart bodyPartHtml=getBodyPart(messageHtml);
            bodyPartText.setHeader("Content-Transfer-Encoding",bodyText.getTransferEncoding());
            bodyPartHtml.setHeader("Content-Transfer-Encoding",bodyHtml.getTransferEncoding());
            mpAlternative.addBodyPart(bodyPartText);
            mpAlternative.addBodyPart(bodyPartHtml);
            mimeMessage.setContent(mpAlternative);

        }else if(pmsg.hasMessageType(MessageType.TEXT)){
            MessageBody bodyText=pmsg.getMessageBody(MessageType.TEXT);
            InputStream inputText=bodyText.getInputStream();
            DataSource messageText=new MimeMessageDataSource(inputText,
                    MessageType.TEXT.getContentType()+"; charset=" + bodyText.getCharset());
            mimeMessage.setDataHandler(new DataHandler(messageText));
            mimeMessage.setHeader("Content-Transfer-Encoding",bodyText.getTransferEncoding());

        }else if(pmsg.hasMessageType(MessageType.HTML)){
            MessageBody bodyHtml=pmsg.getMessageBody(MessageType.HTML);
            InputStream inputHtml=bodyHtml.getInputStream();
            DataSource messageHtml=new MimeMessageDataSource(inputHtml,
                    MessageType.HTML.getContentType()+"; charset="+bodyHtml.getCharset());
            mimeMessage.setDataHandler(new DataHandler(messageHtml));
            mimeMessage.setHeader("Content-Transfer-Encoding",bodyHtml.getTransferEncoding());
        }
        int priority=pmsg.getxPriority();
        if(priority>0 || 6>priority)
            mimeMessage.setHeader("X-Priority",String.valueOf(priority));
        mimeMessage.setSubject(pmsg.getTitle(),pmsg.getCharset());
        return mimeMessage;
    }

    private static BodyPart getBodyPart(DataSource datasource) throws MessagingException {
        BodyPart bp = new MimeBodyPart();
        bp.setDataHandler(new DataHandler(datasource));
        return bp;
    }
}
