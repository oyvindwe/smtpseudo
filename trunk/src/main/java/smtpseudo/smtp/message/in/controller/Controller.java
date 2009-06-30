package smtpseudo.smtp.message.in.controller;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/14
 * Time: 11:21:37
 */
public interface Controller {
    void process(ForwardRequest request) throws Exception;
    String getDumpster();
}
