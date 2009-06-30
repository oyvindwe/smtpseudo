package smtpseudo.smtp.boot;

import org.tanukisoftware.wrapper.WrapperManager;
import java.io.File;
import java.util.List;
import java.util.Iterator;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import smtpseudo.smtp.boot.wrapper.SMTPseudoWrapper;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/28
 * Time: 0:04:31
 */
public class Bootstrap {
    
    public static void main(String[] args) throws Exception {
        prepareBootstrapPhase();
        WrapperManager.start(new SMTPseudoWrapper(),args);
    }

    private static void prepareBootstrapPhase() throws Exception {
        File home=lookupHome();
        addLocalJarFilesToClasspath(home);
    }

    private static File lookupHome() throws Exception {
        File home=null;
        String homeVar=System.getProperty("smtpseudo.home");
        if(homeVar != null && !homeVar.trim().equals("") && !homeVar.equals("%SMTPSEUDO_HOME%"))
            home=new File(homeVar).getCanonicalFile();

        if(home==null || !home.exists() || !home.isDirectory())
            throw new IllegalArgumentException("Either SMTPSEUDO_HOME is not set or does not contain a valid directory.");

        return home;
    }


    public static void addLocalJarFilesToClasspath(File muleHome) throws Exception{
           DefaultClassPathConfig classPath = new DefaultClassPathConfig(muleHome);
           addLibrariesToClasspath(classPath.getURLs());
    }

    public static void addLibrariesToClasspath(List urls) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        ClassLoader sys=ClassLoader.getSystemClassLoader();
        if (!(sys instanceof URLClassLoader)){
            throw new IllegalArgumentException(
                   "PANIC: SMTPseudo has been started with an unsupported classloader: "
                           + sys.getClass().getName());
        }
        URLClassLoader sysCl=(URLClassLoader) sys;
        Class refClass=URLClassLoader.class;
        Method methodAddUrl=refClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        methodAddUrl.setAccessible(true);
        for(Iterator it=urls.iterator(); it.hasNext();){
            URL url = (URL) it.next();
            methodAddUrl.invoke(sysCl, new Object[]{url});
        }
    }
}
