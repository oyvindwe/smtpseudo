package smtpseudo.util.matcher;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/08
 * Time: 23:44:06
 */
public  abstract class ByteArrayExtractorFactory {
    private static final int _1MB=1024*1024*1;
    private final static int DEFAULT_BUFF_SIZE=_1MB;
    private static int buffer=DEFAULT_BUFF_SIZE;
    private static ThreadLocal<ByteArrayExtractor> tLExtractor=new ThreadLocal<ByteArrayExtractor>(){
                @Override
                protected ByteArrayExtractor initialValue(){
                    return new SimpleArbitraryByteArrayExtractor(buffer);
                }
            };

    public static ByteArrayExtractor getByteArrayExtractor(){
        return tLExtractor.get();
    }

    public static void setBuffer(int buffer) {
        ByteArrayExtractorFactory.buffer=buffer;
        tLExtractor=new ThreadLocal<ByteArrayExtractor>(){
                @Override
                protected ByteArrayExtractor initialValue(){
                    return new SimpleArbitraryByteArrayExtractor(ByteArrayExtractorFactory.buffer);
                }
            };
    }

    public static int getBufferSize() {
        return buffer;
    }
}
