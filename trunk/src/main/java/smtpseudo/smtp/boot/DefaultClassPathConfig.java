package smtpseudo.smtp.boot;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class DefaultClassPathConfig{
    protected static final String LIB_DIR = "lib/";
    private List urls = new ArrayList();

    public DefaultClassPathConfig(File home){
        File userDir = new File(home, LIB_DIR);
        this.addFile(userDir);
        this.addFiles(this.listJars(userDir));
    }

    public List getURLs(){
        return new ArrayList(this.urls);
    }

    public void addURLs(List urls){
        if(urls != null && !urls.isEmpty()){
            this.urls.addAll(urls);
        }
    }

    public void addURL(URL url){
        this.urls.add(url);
    }

    public void addFiles(List files){
        for(Iterator i=files.iterator(); i.hasNext();)
            this.addFile((File)i.next());

    }

    public void addFile(File jar){
        try{
            this.addURL(jar.getAbsoluteFile().toURI().toURL());
        }catch (MalformedURLException e){
            throw new RuntimeException("Failed to construct a classpath URL", e);
        }
    }

    protected List listJars(File path){
        File[] jars = path.listFiles(new FileFilter(){
            public boolean accept(File pathname){
                try{
                    return pathname.getCanonicalPath().endsWith(".jar");
                }catch (IOException e){
                    throw new RuntimeException(e.getMessage());
                }
            }
        });
        return jars == null ? Collections.EMPTY_LIST : Arrays.asList(jars);
    }

}

