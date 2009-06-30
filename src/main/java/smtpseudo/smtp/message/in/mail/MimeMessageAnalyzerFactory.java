package smtpseudo.smtp.message.in.mail;

import smtpseudo.smtp.message.MessageType;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.Part;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.Multipart;
import javax.mail.util.SharedByteArrayInputStream;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/15
 * Time: 17:07:38
 * To change this template use File | Settings | File Templates.
 */

public class MimeMessageAnalyzerFactory {
    private static final Log log= LogFactory.getLog(MimeMessageAnalyzerFactory.class);

    public static MimeMessageAnalyzer create(MimeMessage message) throws Exception {
        MimeMessagePart part=new MimeMessagePart(0);
        dumpPart(message,part);
        return new MimeMessagePartAnalyzer(part);
    }
    
    private static class MimeMessagePartAnalyzer implements MimeMessageAnalyzer{
        MimeMessagePart part;
        InputStream streamToReturn;
        
        MimeMessagePartAnalyzer(MimeMessagePart part){
            this.part=part;
        }

        public MimeMessageHeader getHeader(int depth){
            return getHeader(part,depth);
        }

        public InputStream getInputStream(MessageType type,int depth) throws IOException {
            streamToReturn=null;
            setInputStream(part,type,depth);
            if(streamToReturn!=null)
                streamToReturn.reset();
            return streamToReturn;
        }

        private MimeMessageHeader getHeader(MimeMessagePart part,int depth){
            if(part.header!=null && part.depth>=depth)
                return part.header;
            if(part.multipart!=null){
                for(int i=0;i<part.multipart.length;i++)
                    getHeader(part.multipart[i],depth);
            }
            return null;
        }

        private void setInputStream(MimeMessagePart part,MessageType type, int depth){
            String contentType=part.contentType;
            contentType=contentType.toLowerCase();
            log.debug(contentType);
            if(contentType.contains(type.getContentType()) && part.depth>=depth){
                if(streamToReturn==null)
                    streamToReturn=part.data;
            }
            if(part.multipart!=null){
                for(int i=0;i<part.multipart.length;i++)
                    setInputStream(part.multipart[i],type,depth);
            }
        }
    }

    private static void dumpPart(Part p,MimeMessagePart part) throws Exception {
        if(p instanceof Message)
            part.header=dumpHeader((Message)p);
        part.contentType=p.getContentType();
        log.debug("depth:"+part.depth);
        log.debug("contentType:"+part.contentType);
        if(p.isMimeType("text/plain") || p.isMimeType("text/html")){
            InputStream is=p.getInputStream();
            if(ByteArrayInputStream.class.isAssignableFrom(is.getClass()) && is.available()>0)
                part.data=is;
            else if(is.available()>0){
                int bytesRead;
                byte[] tmp=new byte[1024];
                ByteArrayOutputStream array=new ByteArrayOutputStream(tmp.length);
                while((bytesRead=is.read(tmp,0,tmp.length))!=-1)
                    array.write(tmp,0,bytesRead);
                is.close();
                part.data=new SharedByteArrayInputStream(array.toByteArray());
            }
        }else if(p.isMimeType("multipart/*")){
            Multipart mp=(Multipart)p.getContent();
            int count=mp.getCount();
            part.multipart=new MimeMessagePart[count];
            for(int i=0;i<count;i++){
                part.multipart[i]=new MimeMessagePart(part.depth+1);
                dumpPart(mp.getBodyPart(i),part.multipart[i]);
            }
        }else if(p.isMimeType("message/rfc822")){
            dumpPart((Part)p.getContent(),part);
        }
    }
    
    public static class MimeMessagePart{
        int depth;
        MimeMessagePartHeader header;
        String contentType;
        InputStream data;
        MimeMessagePart[] multipart;
        
        MimeMessagePart(int depth){
            this.depth=depth;
        }
    }

    public static class MimeMessagePartHeader implements MimeMessageHeader{
        private List<Address> addressFrom;
        private List<Address> addressTo;
        private List<Address> addressCC;
        private String subject;

        public List<Address> getAddressFrom() {
            return addressFrom;
        }

        public List<Address> getAddressTo() {
            return addressTo;
        }

        public List<Address> getAddressCC() {
            return addressCC;
        }

        public String getSubject() {
            return subject;
        }

        public String toString(){

            return new ToStringBuilder(this)
                .append("From :",getAddressFrom())
                .append("TO :", getAddressTo())
                .append("CC :", getAddressCC())
                .toString();
        }
    }

    private static MimeMessagePartHeader dumpHeader(Message message) throws Exception {
        MimeMessagePartHeader header=new MimeMessagePartHeader();
        Address[]  address;
        if((address=message.getFrom())!=null){
            List<Address> addressFrom=new ArrayList<Address>();
            for(int i=0;i<address.length;i++){
                Address from=address[i];
                log.debug("From:"+from);
                addressFrom.add(from);
            }
            header.addressFrom=addressFrom;
        }
        if((address=message.getRecipients(Message.RecipientType.TO))!=null){
            List<Address> addressTo=new ArrayList<Address>();
            for(int i=0;i<address.length;i++){
                Address to=address[i];
                log.debug("To:"+to);
                addressTo.add(to);
                InternetAddress ia=(InternetAddress)address[i];
                if(ia.isGroup()){
                    InternetAddress[] aa=ia.getGroup(false);
                    for(int j=0;j<aa.length;j++)
                        addressTo.add(aa[j]);
                }
            }
            header.addressTo=addressTo;
        }
        if((address=message.getRecipients(Message.RecipientType.CC))!=null){
            List<Address> addressCC=new ArrayList<Address>();
            for(int i=0;i<address.length;i++){
                Address cc=address[i];
                log.debug("Cc:"+cc);
                addressCC.add(cc);
                InternetAddress ia=(InternetAddress)address[i];
                if(ia.isGroup()){
                    InternetAddress[] aa=ia.getGroup(false);
                    for(int j=0;j<aa.length;j++)
                        addressCC.add(aa[j]);
                }
            }
            header.addressCC=addressCC;
        }
        //If the subject is encoded as per RFC 2047, it is decoded and converted into Unicode
        header.subject=message.getSubject();
        return header;
    }
}
