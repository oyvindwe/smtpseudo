package smtpseudo.util.matcher;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/04
 * Time: 15:19:52
 * To change this template use File | Settings | File Templates.
 */

public class SimpleArbitraryByteArrayExtractor extends ArbitraryByteArrayExtractor implements ByteArrayExtractor{
    private static final int _1MB=1024*1024*1;
    private final static int DEFAULT_BUFF_SIZE=_1MB;
    private ByteBuffer byteBuffer;

    public SimpleArbitraryByteArrayExtractor(){
        this.byteBuffer=ByteBuffer.allocateDirect(DEFAULT_BUFF_SIZE);
    }

    public SimpleArbitraryByteArrayExtractor(int buffsize) {
        this.byteBuffer=ByteBuffer.allocateDirect(buffsize);
    }

    public Map<String,byte[]> match(InputStream in,int start,int end,MatcherBetween matcher) throws IOException {
        allocate(in);
        return match(byteBuffer,matcher,start,end);
    }

    public Map<String,byte[]> match(InputStream in,MatcherBetween matcher) throws IOException {
        allocate(in);
        return match(byteBuffer,matcher,0,byteBuffer.limit());
    }

    public Map<String,byte[]> match(InputStream in,
                                    BoyerMooreByteArrayMatcher head,
                                    String headName,
                                    MatcherBetween matcher,
                                    BoyerMooreByteArrayMatcher tail,
                                    String tailName) throws IOException{
        allocate(in);
        return match(byteBuffer,head,headName,matcher,tail,tailName,0,byteBuffer.limit());
    }

    public Map<String,byte[]> match(InputStream in,
                                    BoyerMooreByteArrayMatcher head,
                                    String headName,
                                    MatcherBetween matcher,
                                    BoyerMooreByteArrayMatcher tail,
                                    String tailName,int start,int end) throws IOException{
        allocate(in);
        return match(byteBuffer,head,headName,matcher,tail,tailName,start,end);
    }

    public byte[] headToMatch(InputStream in,BoyerMooreByteArrayMatcher matcher) throws IOException {
        allocate(in);
        return head(byteBuffer,matcher);
    }

    public byte[] tailToMatch(InputStream in,BoyerMooreByteArrayMatcher matcher) throws IOException {
        allocate(in);
        return tail(byteBuffer,matcher);
    }

    public int indexOf(InputStream in,BoyerMooreByteArrayMatcher matcher) throws IOException {
        allocate(in);
        return indexOf(byteBuffer,matcher,0,byteBuffer.limit());
    }

    private void allocate(InputStream in) throws IOException {
        byteBuffer.clear();
        int bytesRead;
        byte[] tmp=new byte[1024];
        ByteArrayOutputStream outbuf=new ByteArrayOutputStream(tmp.length);
        while((bytesRead=in.read(tmp,0,tmp.length))!=-1)
            outbuf.write(tmp,0,bytesRead);
        in.close();
        byteBuffer.put(outbuf.toByteArray());
        byteBuffer.flip();
    }
}
