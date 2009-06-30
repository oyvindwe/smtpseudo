package smtpseudo.smtp.message.in.command;

import smtpseudo.util.matcher.ByteArrayExtractor;
import smtpseudo.util.matcher.ByteArrayExtractorFactory;
import smtpseudo.util.matcher.MatcherBetween;
import smtpseudo.util.matcher.BoyerMooreByteArrayMatcher;
import java.io.InputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/07
 * Time: 21:16:15
 */
public class StringExtractCommandImpl implements StringExtractCommand{
    private BoyerMooreByteArrayMatcher head;
    private BoyerMooreByteArrayMatcher tail;
    private MatcherBetween matcher;
    private String encoding;
    private String scope;

    public StringMatchResult execute(InputStream in) throws IOException {
        ByteArrayExtractor extractor= ByteArrayExtractorFactory.getByteArrayExtractor();
        Map<String,byte[]> result;
        synchronized(this){
            String[] nums;
            if(scope!=null && scope.length()>0 &&
                    (nums=scope.split(","))!=null && nums.length==2){
                try{
                    int start=Integer.parseInt(nums[0]);
                    int end=Integer.parseInt(nums[1]);
                    result=extractor.match(in,head,"head",matcher,tail,"tail",start,end);
                }catch(NumberFormatException e){
                    result=extractor.match(in,head,"head",matcher,tail,"tail");
                }
            }else
                result=extractor.match(in,head,"head",matcher,tail,"tail");
        }
        return new StringMatchResult(result,encoding);
    }

    public void addAround(String id,BoyerMooreByteArrayMatcher bmBegin,BoyerMooreByteArrayMatcher bmEnd) {
        if(matcher==null)
            matcher=new MatcherBetween();
        matcher.add(id,bmBegin,bmEnd);
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setHead(BoyerMooreByteArrayMatcher head) {
        this.head = head;
    }

    public void setTail(BoyerMooreByteArrayMatcher tail) {
        this.tail = tail;
    }
}
