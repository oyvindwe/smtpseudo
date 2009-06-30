package smtpseudo.smtp.message.out.mail;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/11
 * Time: 9:42:22
 * To change this template use File | Settings | File Templates.
 */
public class StreamConverter {

    public static InputStream convert(InputStream in,String encodeFrom,String encodeTo) throws IOException { 
        Reader reader=new BufferedReader(new InputStreamReader(in,encodeFrom));
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        Writer writer=new BufferedWriter(new OutputStreamWriter(out,encodeTo));
        int charsRead;
        char[] tmp=new char[1024];
        while((charsRead=reader.read(tmp,0,tmp.length))!=-1)
            writer.write(tmp,0,charsRead);
        reader.close();
        writer.close();
        return new ByteArrayInputStream(out.toByteArray());
    }    
}
