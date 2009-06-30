package smtpseudo.util.matcher;

/**
 * Created by IntelliJ IDEA.
 * User: hanishi
 * Date: 2009/06/04
 * Time: 9:38:04
 * To change this template use File | Settings | File Templates.
 */
public class BoyerMooreByteArrayMatcher {
    private final int[] pattern;
    private int[] skip;

    public BoyerMooreByteArrayMatcher(byte[] target){
        pattern=new int[target.length];
        for(int i=0;i<target.length;i++)
            pattern[i]=target[i] & 0xff;
        if(pattern.length>1)
            this.skip=getSkipArray(target);
    }

    int length(){
        return pattern.length;
    }

    int get(int i){
        return pattern[i];
    }

    int skip(int i){
        return skip==null ? 1 : skip[i];
    }

    private static int[] getSkipArray(byte[] pattern){
        int TABLE_SIZE=255;
        int[] skip=new int[TABLE_SIZE+1];
        int i;
        for(i=0; i<=TABLE_SIZE;i++)
            skip[i]=pattern.length;
        for(i=0; i<pattern.length-1;i++)
            skip[pattern[i] & 0xff]=pattern.length -1 -i;
        return skip;
    }
}
