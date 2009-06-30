package smtpseudo.util.template;

import java.nio.ByteBuffer;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/05/25
 * Time: 11:06:12
 * To change this template use File | Settings | File Templates.
 */
public class DocumentTemplateFacotry extends AbstractDocumentTemplateFactory {
    private static final int _1MB=1024*1024*1;
    private final static int DEFAULT_BUFF_SIZE=_1MB;
    private ByteBuffer byteBuffer;

    public DocumentTemplateFacotry(){
        super();
        this.byteBuffer=ByteBuffer.allocateDirect(DEFAULT_BUFF_SIZE);
    }
    
    public DocumentTemplateFacotry(int size){
        super();
        this.byteBuffer=ByteBuffer.allocateDirect(size);
    }

    public synchronized DocumentTemplate createTemplate(InputStream in) throws IOException {
        byteBuffer.clear();
        int bytesRead;
        byte[] tmp=new byte[1024];
        ByteArrayOutputStream outbuf=new ByteArrayOutputStream(tmp.length);
        while((bytesRead=in.read(tmp,0,tmp.length))!=-1)
            outbuf.write(tmp,0,bytesRead);
        in.close();
        byteBuffer.put(outbuf.toByteArray());
        byteBuffer.flip();
        return createTemplate(byteBuffer);
    }
}
