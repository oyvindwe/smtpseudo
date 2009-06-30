package smtpseudo.util.template;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractDocumentTemplateFactory {
	private static final int[] open={'$','{'},close={'}','$'};
    private static final int TABLE_SIZE=255;
    private static final int SKIP_LEN=2;
	private static int[] skip=new int[TABLE_SIZE+1];
    private final static int DEFAULT_MAX_VARIABLE_SIZE=1024;
	
	protected AbstractDocumentTemplateFactory(){
		for(int i=0; i<=TABLE_SIZE;i++)
			skip[i]=SKIP_LEN;
    }
	
    protected static boolean VARCHAR(ByteBuffer src){
    	if(src.position()==src.limit())
    		return false;
    	int pos=src.position();
    	int code;
    	if(0x30<=(code=(src.get() & 0xff)) && code<=0x39 || code>=0x41 && code<=0x5a||
                 							  code>=0x61 && code<=0x7a||
                 							  code==0x5f)
    		return true;
    	src.position(pos);
		return false;
    }
    
    protected static String getVariable(ByteBuffer src){
    	int pos=src.position();
		while(VARCHAR(src))
			if(src.position()==src.limit()){
				src.position(pos);
                int size=src.remaining();
                ByteBuffer key_buf=ByteBuffer.allocate(size).put(src);
				return new String(key_buf.array());
			}
		return null;
    }

    protected synchronized static final DocumentTemplate createTemplate(ByteBuffer src){
        return createTemplate(src,DEFAULT_MAX_VARIABLE_SIZE);
    }

    protected synchronized static final DocumentTemplate createTemplate(ByteBuffer src, int max_variable_size){
        int limit_org=src.limit();
        int pos=0;
        int mark=0;
        int i=pos+SKIP_LEN-1;
        boolean flag=false;
        skip[open[0]]=SKIP_LEN-1;
        int[] serach_pattern=open;
        int len=serach_pattern.length;
        Map<String,ByteBuffer> variables=new HashMap<String,ByteBuffer>();
        List<ByteBuffer> bufflist=new LinkedList<ByteBuffer>();
        ByteBuffer buffer=ByteBuffer.allocateDirect(src.capacity());
        while(i<limit_org) {         
             boolean found=false;
             int tmp=(src.get(i) & 0xff);
             if(tmp==serach_pattern[len-1]) {
                 int j=len-1, k=i;
                 while(serach_pattern[--j]==(src.get(--k) & 0xff))
                     if(j==0){
                        found=true;
                        flag=!flag;
                        if(flag){
                           mark=k;
                           serach_pattern=close;
                           skip[open[0]]=SKIP_LEN;
                           skip[close[0]]=SKIP_LEN-1;
                        }else{
                            if(mark!=0){
                                src.position(pos).limit(mark);
                                int position=buffer.position();
                                buffer.put(src);
                                buffer.flip().position(position);
                                bufflist.add(buffer.slice());
                                position=buffer.limit();
                                buffer.clear();
                                buffer.position(position);
                                src.clear();
                            }
                            src.position(mark+len).limit(k);
                            String key=getVariable(src);
                            src.position(mark).limit(++i);
                            if(key!=null){
                            	ByteBuffer variable;
                            	if(!variables.containsKey(key)){
                            		variable=ByteBuffer.allocate(max_variable_size);
                            		variable.put(src).flip();
                            		variables.put(key, variable);
                            	}else
                            		variable=variables.get(key);
                                bufflist.add(variable);
                            }else{
                                int position=buffer.position();
                                buffer.put(src);
                                buffer.flip().position(position);
                                bufflist.add(buffer.slice());
                                position=buffer.limit();
                                buffer.clear();
                                buffer.position(position);
                            }
                            pos=i;
                            serach_pattern=open;
                            skip[close[0]]=SKIP_LEN;
                            skip[open[0]]=SKIP_LEN-1;
                            src.limit(limit_org);
                        }
                        i=i+len-1;
                        break;
                     }
             }
             if(!found)
                i += skip[tmp];
        }
        if(pos!=limit_org){
            src.position(pos);
            int position=buffer.position();
            buffer.put(src);
            buffer.flip().position(position);
            bufflist.add(buffer.slice());
        }
        src.clear();
        return new DocumentTemplate(bufflist,variables);
	}
}
