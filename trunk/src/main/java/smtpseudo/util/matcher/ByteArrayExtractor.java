package smtpseudo.util.matcher;

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/08
 * Time: 23:49:14
 */
public interface ByteArrayExtractor {
    Map<String,byte[]> match(InputStream in,int start,int end,MatcherBetween matcher) throws IOException;
    Map<String,byte[]> match(InputStream in,MatcherBetween matcher) throws IOException;
    Map<String,byte[]> match(InputStream in,
                             BoyerMooreByteArrayMatcher head,
                             String headName,
                             MatcherBetween matcher,
                             BoyerMooreByteArrayMatcher tail,
                             String tailName,int start,int end) throws IOException;
    Map<String,byte[]> match(InputStream in,
                             BoyerMooreByteArrayMatcher head,
                             String headName,
                             MatcherBetween matcher,
                             BoyerMooreByteArrayMatcher tail,
                             String tailName) throws IOException;
    byte[] headToMatch(InputStream in,BoyerMooreByteArrayMatcher matcher) throws IOException;
    byte[] tailToMatch(InputStream in,BoyerMooreByteArrayMatcher matcher) throws IOException;
    int indexOf(InputStream in,BoyerMooreByteArrayMatcher matcher) throws IOException;
}
