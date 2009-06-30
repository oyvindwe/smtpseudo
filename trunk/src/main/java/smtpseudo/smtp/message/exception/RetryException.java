package smtpseudo.smtp.message.exception;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/21
 * Time: 23:38:55
 */
public class RetryException extends Exception{
    public RetryException(String message){
        super(message);
    }
}
