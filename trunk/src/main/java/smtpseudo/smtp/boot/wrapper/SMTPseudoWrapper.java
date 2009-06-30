package smtpseudo.smtp.boot.wrapper;

import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;
import org.apache.commons.logging.Log;
import java.util.Properties;
import java.util.Date;
import java.io.IOException;
import java.io.File;
import smtpseudo.smtp.message.Resources;
import smtpseudo.smtp.SMTPseudo;
import javax.mail.Session;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/28
 * Time: 7:45:39
 */
public class SMTPseudoWrapper implements WrapperListener {
    private static String connectTo="127.0.0.1";
    private static int port=1025;
    private static int threads=Runtime.getRuntime().availableProcessors();
    private static Log log;
    private SMTPseudo pseudo;
    
    public Integer start(String[] strings) {
        Properties pseudoConfig;
        try {
            pseudoConfig = Resources.getResourceAsProperties("conf/smtpseudo.properties");
        } catch (IOException e) {
            e.printStackTrace();
            return new Integer(1);
        }
        connectTo=pseudoConfig.getProperty("smtpseudo.connect");//smtp server
        port=Integer.parseInt(pseudoConfig.getProperty("smtpseudo.port"));//my port
        threads=Integer.parseInt(pseudoConfig.getProperty("smtpseudo.forwarders"));//worker threads
        String mydomain=pseudoConfig.getProperty("smtpseudo.mydomain");//my domain
        String[] allowedRemote=pseudoConfig.getProperty("smtpseudo.acceptdomain").split(",");//accept domain
        String home=System.getProperty("smtpseudo.home");
        File errDump=new File(home,"failed");//logfile
        if(!errDump.exists())
            errDump.mkdir();
        Properties props=new Properties();
        props.setProperty("mail.host",connectTo);
		pseudo=new SMTPseudo(port,threads, Session.getDefaultInstance(props),mydomain,allowedRemote,errDump);
        log=pseudo.getLogger();
        if(mydomain.length()>0 && allowedRemote.length>0){
            File[] files;
            if((files=errDump.listFiles())!=null && files.length>0){
                log.error("\nThere are messages which could not be forwarded due to system error occured on "+ new Date(errDump.lastModified())+
                "\nsmtpseudo will not start until all messages gets removed from "+errDump);
                return new Integer(1);
            }else
		        pseudo.start();
            return null;
        }else{
            log.error("could not start service. smtpseudo.mydomain and smtpseudo.acceptdomain " +
                    "in smtpseudo.properties are required.");
            return new Integer(1);
        }
    }

    public int stop(int exitCode) {
        if(log!=null && log.isInfoEnabled())
            log.info("shutdown requested.");
        else
            System.out.println("shutdown requested.");
        pseudo.halt();
        return exitCode;
    }

    public void controlEvent(int event) {
        if (!WrapperManager.isControlledByNativeWrapper()){
            if ((event==WrapperManager.WRAPPER_CTRL_C_EVENT) ||
                    (event==WrapperManager.WRAPPER_CTRL_CLOSE_EVENT)||
                    (event==WrapperManager.WRAPPER_CTRL_SHUTDOWN_EVENT))
                WrapperManager.stop(0);
        }
    }
}
