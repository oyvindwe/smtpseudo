package smtpseudo.smtp.message.in.controller.impl;

import org.apache.commons.chain.impl.ChainBase;
import smtpseudo.smtp.message.in.controller.Controller;
import smtpseudo.smtp.message.in.controller.ForwardRequest;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/14
 * Time: 11:43:57
 */
public class ForwardChain extends ChainBase implements Controller {
    private String dumpster;

    public void addForwarder(Forwarder forwarder) {
        this.addCommand(forwarder);
    }

    public void process(ForwardRequest request) throws Exception {
        this.execute(request);
    }

    public String getDumpster() {
        return dumpster;
    }

    public void setDumpster(String dumpster) {
        this.dumpster = dumpster;
    }
}
