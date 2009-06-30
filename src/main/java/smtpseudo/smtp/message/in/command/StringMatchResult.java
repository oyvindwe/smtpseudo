package smtpseudo.smtp.message.in.command;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/08
 * Time: 13:55:31
 * To change this template use File | Settings | File Templates.
 */
public class StringMatchResult {
    private final Map<String,byte[]> result;
    private final String encoding;
    
    StringMatchResult(Map<String,byte[]> result,String encoding){
        this.result=result;
        this.encoding=encoding;
    }

    public String get(String key) {
        byte[] value=result.get(key);
        if(value==null)
            return null;
        try {
            return new String(value,encoding).trim();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public String toString(){
        StringBuilder builder=new StringBuilder();
        for(Map.Entry<String,byte[]> entry:result.entrySet()){
            try {
                builder.append("key:"+entry.getKey()+",value:"+new String(entry.getValue(),encoding));
            } catch (UnsupportedEncodingException e) {
                
            }
            builder.append(",");
        }
        return builder.toString();
    }

}
