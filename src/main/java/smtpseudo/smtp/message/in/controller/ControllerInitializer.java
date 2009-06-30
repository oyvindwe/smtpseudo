package smtpseudo.smtp.message.in.controller;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smtpseudo.smtp.message.out.mail.*;
import smtpseudo.smtp.message.exception.ValidationException;
import smtpseudo.smtp.message.in.controller.impl.ForwardChain;
import smtpseudo.smtp.message.in.controller.impl.Forwarder;
import smtpseudo.smtp.message.in.controller.impl.forwarder.FromAddressForwarder;
import smtpseudo.smtp.message.in.controller.impl.forwarder.TitleStringSignatureForwarder;
import smtpseudo.smtp.message.in.controller.impl.forwarder.BodyStringSignatureForwarder;
import smtpseudo.util.matcher.BoyerMooreByteArrayMatcher;
import java.util.Collection;
import java.util.ArrayList;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/15
 * Time: 13:13:10
 * To change this template use File | Settings | File Templates.
 */
public class ControllerInitializer extends DefaultHandler {
    private static ControllerInitializer instance=new ControllerInitializer();
    private static Digester digester;
    private static ForwardChain forwardChain=new ForwardChain();
    private static final String CONF_FILE="mail-forwarder.xml";
    protected static Collection validationExceptions = new ArrayList();
    private static Forwarder current;
    private static final Log log= LogFactory.getLog(ControllerInitializer.class);
    static{
       try{
            digester=new Digester();
            digester.setNamespaceAware(false);
            digester.setUseContextClassLoader(true);
            digester.setValidating(true);
            digester.setErrorHandler(ControllerInitializer.getInstance());
            digester.push(ControllerInitializer.getInstance());
            digester.addRule("mail-forwarder",new SetDumpsterRule());
            digester.addRule("mail-forwarder/forwarder/ifFrom",new IfFromForwarderCreateRule());
            IfFoundForwaderCreateRule ifFound=new IfFoundForwaderCreateRule();
            digester.addRule("mail-forwarder/forwarder/ifFound/inTitle",ifFound);
            digester.addRule("mail-forwarder/forwarder/ifFound/inText",ifFound);
            digester.addRule("mail-forwarder/forwarder/ifFound/inHtml",ifFound);
            digester.addRule("mail-forwarder/forwarder/mail",new SetTemplateMessageBuilderRule());
            digester.addRule("mail-forwarder/forwarder/address",new SetAddressRule());
            digester.addRule("mail-forwarder/forwarder/lookup",new SetLookupRule());
            digester.addRule("mail-forwarder/forwarder",new EndForwarderRule());
            validationExceptions.clear();
            URL url = Thread.currentThread().getContextClassLoader().getResource(CONF_FILE);
            InputSource is=new InputSource(url.toExternalForm());
            digester.parse(is);
            if (validationExceptions.size() > 0)
                throw new ValidationException(validationExceptions);

        }catch(Throwable e){
            throw new ExceptionInInitializerError(e);
        }
    }

    public void warning(SAXParseException e) throws SAXException {
        addValidationException(e);
    }

    public void error(SAXParseException e) throws SAXException {
        addValidationException(e);
    }

    public void fatalError(SAXParseException e) {
        addValidationException(e);
    }

    public void addValidationException(SAXException e) {
        validationExceptions.add(e);
    }

    private ControllerInitializer() {}

    public static ControllerInitializer getInstance(){
        return instance;
    }

    public static Controller getController() {
        return forwardChain;
    }

    private static void setAddress(String[] addresses,String type){
        if(type.equals("TO")){
            for(String address:addresses)
                current.addToAddress(address);
        }else if(type.equals("CC")){
            for(String address:addresses)
                current.addCcAddress(address);
        }else if(type.equals("BCC")){
            for(String address:addresses)
                current.addBccAddress(address);
        }
    }

    private static class SetDumpsterRule extends Rule{
        String dumpster;
        @Override
        public void begin(String namespace,String name,Attributes attributes) throws Exception{
            dumpster=attributes.getValue("dumpster");
        }

        @Override
        public void finish(){
            forwardChain.setDumpster(dumpster);
        }
    }

    private static class EndForwarderRule extends Rule{
        private String fromAddress;

        @Override
        public void begin(String namespace,String name,Attributes attributes) throws Exception{
            fromAddress=attributes.getValue("from");
        }
        
        @Override
        public void end(String namespace, String name) {
            current.setMailFrom(fromAddress);
            log.info("Forwarder:"+current+ " created");
            current=null;
        }
    }

    private static class SetTemplateMessageBuilderRule extends Rule {
        @Override
        public void begin(String namespace,String name,Attributes attributes) throws Exception {
            TemplateMessageBuilder builder=new TemplateMessageBuilder();
            builder.setTemplate(attributes.getValue("useTemplate"));
            builder.setCmdOnText(attributes.getValue("cmdOnText"));
            builder.setCmdOnHtml(attributes.getValue("cmdOnHtml"));
            current.setMessageBuilder(builder);
        }
    }

    private static class SetAddressRule extends Rule {
        private String type;
        @Override
        public void begin(String namespace,String name,Attributes attributes) throws Exception{
            type=attributes.getValue("type");
        }
        
        @Override
        public void body(String namespace, String name, String text){
            if(text!=null && text.length()>0){
                String[] lines=text.split("\n");
                StringBuilder str=new StringBuilder();
                for(int i=0;i<lines.length;i++)
                    str.append(lines[i].trim());
                setAddress(str.toString().split(","),type);
            }
        }
    }

    private static class SetLookupRule extends Rule {
        public void begin(String namespace,String name,Attributes attributes)throws Exception{
             current.setLookup(attributes.getValue("field"));
        }
    }

    private static class IfFromForwarderCreateRule extends Rule {

        @Override
        public void body(String namespace,String name,String text) throws Exception {
            String[] address=null;
            if(text!=null && text.length()>0){
                String[] lines=text.split("\n");
                StringBuilder str=new StringBuilder();
                for(int i=0;i<lines.length;i++)
                    str.append(lines[i].trim());
                address=str.toString().split(",");
            }
            FromAddressForwarder fd=new FromAddressForwarder();
            fd.setIfFrom(address);
            forwardChain.addForwarder(fd);
            current=fd;
        }
    }

    private static class IfFoundForwaderCreateRule extends Rule {
        private String charset;
        
        @Override
        public void begin(String namespace,String name,Attributes attributes) throws Exception {
            charset=attributes.getValue("charset");
        }

        @Override
        public void body(String namespace,String name,String text) throws Exception {
            if(text!=null && text.length()>0){
                if(name.equals("inTitle")){
                    TitleStringSignatureForwarder forwarder=new TitleStringSignatureForwarder();
                    forwarder.setIfInTitle(text);
                    forwardChain.addForwarder(forwarder);
                    current=forwarder;
                }else if(name.equals("inText")||name.equals("inHtml")){
                    Forwarder forwarder=current;
                    BodyStringSignatureForwarder bssf;
                    if(forwarder!=null && forwarder instanceof BodyStringSignatureForwarder)
                         bssf=(BodyStringSignatureForwarder)forwarder;
                    else{
                        bssf=new BodyStringSignatureForwarder();
                        forwardChain.addForwarder(bssf);
                        current=bssf;
                    }
                    if(name.equals("inText"))
                        bssf.setIfInText(new BoyerMooreByteArrayMatcher(text.getBytes(charset)));
                    if(name.endsWith("inHtml"))
                        bssf.setIfInHtml(new BoyerMooreByteArrayMatcher(text.getBytes(charset)));
                }
            }

        }
    }
    
}