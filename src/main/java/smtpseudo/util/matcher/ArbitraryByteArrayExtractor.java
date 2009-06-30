package smtpseudo.util.matcher;

import java.nio.ByteBuffer;
import java.util.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/04
 * Time: 9:21:27
 * To change this template use File | Settings | File Templates.
 */
public abstract class ArbitraryByteArrayExtractor {
    
    protected static final Map<String,byte[]> match(ByteBuffer src, MatcherBetween matcher){
        return match(src,matcher,src.position(),src.limit());
    }

    protected static byte[] head(ByteBuffer src,BoyerMooreByteArrayMatcher matcher){
           return match(src,matcher,0,src.limit(),true);
    }

    protected static byte[] tail(ByteBuffer src,BoyerMooreByteArrayMatcher matcher){
        return match(src,matcher,0,src.limit(),false);
    }

    protected static int indexOf(ByteBuffer src,BoyerMooreByteArrayMatcher matcher,int start,int end){
        if(start<0 || start>=end || end>src.limit())
            throw new IndexOutOfBoundsException("start:"+start+" end:"+end);
        int limit_org=end;
        int pos=start;
        int shift=matcher.length();
        int i=pos+shift-1;
        while(i<limit_org) {
            int tmp=(src.get(i) & 0xff);
            if(tmp==matcher.get(matcher.length()-1)) {
                int j=matcher.length()-1, k=i;
                while(j==0 || matcher.get(--j)==(src.get(--k) & 0xff))
                    if(j==0)
                        return k;
            }
            i += matcher.skip(tmp);
        }
        return -1;
    }

    protected Map<String,byte[]> match(ByteBuffer src,
                                    BoyerMooreByteArrayMatcher head,
                                    String headName,
                                    MatcherBetween matcher,
                                    BoyerMooreByteArrayMatcher tail,
                                    String tailName,int start,int end) throws IOException {
        int betweenPosition=start;
        int limit_org=end;
        byte[] headByte=null;
        byte[] tailByte=null;
        if(head!=null && headName!=null){
            headByte=match(src,head,start,end,true);
            if(headByte!=null)
                betweenPosition=src.limit()+head.length();
            src.clear().limit(limit_org);
        }
        Map<String,byte[]> between=match(src,matcher,betweenPosition,src.limit());
        if(tail!=null && tailName!=null){
            tailByte=match(src,tail,src.position(),src.limit(),false);
        }
        if(headByte!=null)
            between.put(headName,headByte);
        if(tailByte!=null)
            between.put(tailName,tailByte);
        return between;
    }

    protected static byte[] match(ByteBuffer src,BoyerMooreByteArrayMatcher matcher,
                                    int start,int end, boolean prefix){
        if(start<0 || start>=end || end>src.limit())
            throw new IndexOutOfBoundsException("start:"+start+" end:"+end);
        int limit_org=end;
        int pos=start;
        int shift=matcher.length();
        int i=pos+shift-1;
        while(i<limit_org) {
            int tmp=(src.get(i) & 0xff);
            if(tmp==matcher.get(matcher.length()-1)) {
                int j=matcher.length()-1, k=i;
                while(j==0 || matcher.get(--j)==(src.get(--k) & 0xff))
                    if(j==0){
                        if(prefix)
                            src.position(pos).limit(k);
                        else
                            src.position(++i);
                        int size;
                        byte[] extracted=null;
                        if((size=src.remaining())>0){
                            extracted=new byte[size];
                            src.get(extracted);
                        }
                        return extracted;
                    }
            }
            i += matcher.skip(tmp);
        }
        return null;
    }

    protected static final Map<String,byte[]> match(ByteBuffer src, MatcherBetween matcher, int start,int end){
        if(start<0 || start>=end || end>src.limit())
            throw new IndexOutOfBoundsException("start:"+start+" end:"+end);
        int limit_org=end;
        int shift=matcher.length();
        int i=start+shift-1;
        int mark=0;
        boolean flag=false;
        Map<String,byte[]> extracted=new HashMap<String,byte[]>();
        while(i<limit_org) {
            boolean found=false;
            int tmp=(src.get(i) & 0xff);
            if(tmp==matcher.get(matcher.length()-1)) {
                int j=matcher.length()-1, k=i;
                while(j==0 || matcher.get(--j)==(src.get(--k) & 0xff))
                    if(j==0){
                        found=true;
                        flag=!flag;
                        if(flag){
                            mark=k;
                            matcher.swap();
                        }else{
                            src.position(mark + shift).limit(k);
                            if(src.remaining()>0){
                                ByteBuffer target=src.slice();
                                int size=target.remaining();
                                byte[] array=new byte[size];
                                target.get(array);
                                String name=matcher.getName();
                                if(name!=null && name.length()>0)
                                    extracted.put(name,array);
                            }
                            matcher.swap();
                            matcher.nextMatcher();
                            shift=matcher.length();
                            src.limit(limit_org);
                            src.position(i+matcher.length());
                        }
                        i=i+matcher.length();
                        break;
                    }
            }
            if(!found)
                i += matcher.skip(tmp);
        }
        matcher.reset();
        return extracted;
    }
}