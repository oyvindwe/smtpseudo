package smtpseudo.smtp.message.in.command;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;
import org.apache.commons.digester.*;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.util.*;
import java.net.URL;
import java.io.UnsupportedEncodingException;
import smtpseudo.util.matcher.BoyerMooreByteArrayMatcher;
import smtpseudo.util.matcher.ByteArrayExtractorFactory;
import smtpseudo.smtp.message.exception.ValidationException;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/07
 * Time: 21:07:58
 */

public class StringExtractCommandFactory extends DefaultHandler {
    private static final StringExtractCommandFactory instance=new StringExtractCommandFactory();
    private static Digester digester;
    private static HashMap<String,StringExtractCommand> commands=new HashMap<String,StringExtractCommand>();
    private static final String CONF_FILE="string-extract-command.xml";
    protected static Collection validationExceptions=new ArrayList();
    private static final Log log= LogFactory.getLog(StringExtractCommandFactory.class);

    static{
        try{
            digester=new Digester();
            digester.setNamespaceAware(false);
            digester.setUseContextClassLoader(true);
            digester.setValidating(true);
            digester.setErrorHandler(StringExtractCommandFactory.getInstance());
            digester.push(StringExtractCommandFactory.getInstance());
            digester.addSetProperties("string-extract-command","receive_buffer","buffer");
            digester.addRuleSet(new CommandRuleset("string-extract-command/command"));
            validationExceptions.clear();
            URL url = Thread.currentThread().getContextClassLoader().getResource(CONF_FILE);
            InputSource is=new InputSource(url.toExternalForm());
            digester.parse(is);
            if (validationExceptions.size() > 0)
                throw new ValidationException(validationExceptions);
            log.debug("message template initialized. Allocatable buffer size for each message received is "
                    +StringExtractCommandFactory.getInstance().getBuffer());
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

    private StringExtractCommandFactory(){}

    public static StringExtractCommandFactory getInstance() {
        return instance;
    }

    public static void put(String id,StringExtractCommand command){
        commands.put(id,command);
    }

    public static StringExtractCommand get(String id){
        return commands.get(id);
    }

    public int getBuffer() {
        return ByteArrayExtractorFactory.getBufferSize();
    }

    public void setBuffer(int buffer) {
        ByteArrayExtractorFactory.setBuffer(buffer);
    }

    public static class CommandRuleset extends RuleSetBase {
        private String prefix;
        public CommandRuleset(String prefix){
            super();
            this.prefix=prefix;
        }

        @Override
        public void addRuleInstances(Digester digester) {
            digester.addObjectCreate(prefix, StringExtractCommandImpl.class);
            digester.addRule(prefix,new CallMethodRule(1,"put",2,new Class[]{String.class,StringExtractCommandImpl.class}));
            digester.addCallParam(prefix,0,"id");
            digester.addCallParam(prefix,1,true);
            digester.addSetProperties(prefix,"encoding","encoding");
            CreateHeadTailRule createHeadTailRule=new CreateHeadTailRule();
            digester.addRule(prefix+"/head/suffix",createHeadTailRule);
            digester.addSetNext(prefix+"/head/suffix","setHead");
            CreateAroundRule createAroundRule=new CreateAroundRule();
            digester.addRule(prefix+"/around/prefix",createAroundRule);
            digester.addRule(prefix+"/around/suffix",createAroundRule);
            digester.addRule(prefix+"/around", new MatcherBetweenSetRule());   
            digester.addRule(prefix+"/tail/prefix",createHeadTailRule);
            digester.addSetNext(prefix+"/tail/prefix","setTail");
        }
    }

    public static class MatcherBetweenSetRule extends Rule {
        private String matcherName;
        
        @Override
        public void begin(String namespace,String name,Attributes attributes)throws Exception{
            matcherName=attributes.getValue("name");
        }

        @Override
        public void end(String namespace,String name) throws Exception {
            BoyerMooreByteArrayMatcher bmEnd=(BoyerMooreByteArrayMatcher)digester.pop();
            BoyerMooreByteArrayMatcher bmBegin=(BoyerMooreByteArrayMatcher)digester.pop();
            StringExtractCommandImpl command=(StringExtractCommandImpl)digester.peek(digester.getCount()-2);
            command.addAround(matcherName,bmBegin,bmEnd);
        }
    }

    public static class CreateAroundRule extends Rule {

        @Override
        public void body(String namespace, String name, String text){
            StringExtractCommandImpl command=(StringExtractCommandImpl)digester.peek(digester.getCount()-2);
            String encoding=command.getEncoding();
            text=text.replaceAll("<SPACE>"," ")
                    .replaceAll("<TAB>","\t")
                    .replaceAll("<CR>","\r")
                    .replaceAll("<LF>","\n");
            try {
                digester.push(new BoyerMooreByteArrayMatcher(text.getBytes(encoding)));
            } catch (UnsupportedEncodingException ignored) {

            }
        }
    }

    public static class CreateHeadTailRule extends Rule {
        @Override
        public void body(String namespace, String name, String text){
            StringExtractCommandImpl command=(StringExtractCommandImpl)digester.peek(digester.getCount()-2);
            String encoding=command.getEncoding();
            text=text.replaceAll("<SPACE>"," ")
                    .replaceAll("<TAB>","\t")
                    .replaceAll("<CR>","\r")
                    .replaceAll("<LF>","\n");
            try {
                digester.push(new BoyerMooreByteArrayMatcher(text.getBytes(encoding)));
            } catch (UnsupportedEncodingException ignored) {

            }
        }

        public void end(String namespace, String name)throws Exception{
            digester.pop();
        }
    }
}
