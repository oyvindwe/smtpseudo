package smtpseudo.util.template;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channels;
import java.util.Set;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/10
 * Time: 10:05:59
 * To change this template use File | Settings | File Templates.
 */
public class DocumentTemplate {
    private List<ByteBuffer> bufflist;
    private Map<String,ByteBuffer> variables;

    DocumentTemplate(List<ByteBuffer> bufflist,Map<String,ByteBuffer> variables){
        this.bufflist=bufflist;
        this.variables=variables;
    }
    
    public InputStream getInputStream(){
        byte[] streambuf=new byte[32];
        int count=0;
        for(ByteBuffer byteBuffer:bufflist){
        	byteBuffer.rewind();
            int size=byteBuffer.remaining();
            byte[] source_array=new byte[size];
            byteBuffer.get(source_array);
            int newcount=count+size;
            if(newcount>streambuf.length){
                byte newbuf[]=new byte[Math.max(streambuf.length<<1,newcount)];
                System.arraycopy(streambuf,0,newbuf,0,count);
                streambuf=newbuf;
            }
            System.arraycopy(source_array,0,streambuf,count,size);
            count=newcount;
        }
        return new ByteArrayInputStream(streambuf,0,count);
    }

    public void setVariable(String target, byte[] element)throws IOException {
        if(variables.containsKey(target)){
        	ByteBuffer byteBuffer=variables.get(target);
        	byteBuffer.clear();
            int min_size=Math.min(byteBuffer.capacity(),element.length);
            byteBuffer.put(element,0,min_size).limit(min_size);
        }
    }

    public void writeTo(OutputStream out) throws IOException{
        WritableByteChannel outchannel= Channels.newChannel(out);
            for(ByteBuffer byteBuffer:bufflist){
            	byteBuffer.rewind();
                outchannel.write(byteBuffer);
            }
        if(!(out.equals(System.out)||out.equals(System.err))){
        	out.close();
        	outchannel.close();
        }
    }

	public Set<String> getVariables() {
		return variables.keySet();
	}
}
