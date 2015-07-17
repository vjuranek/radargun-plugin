package org.jenkinsci.plugins.radargun.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class IOUtils {
    
    public static final String EOF_REG_EXP = "\\A";
    
    /**
     * 
     * @param resource path
     * @return resource loaded as a String
     * @throws IOException
     */
    public static String loadResourceAsString(String path) throws IOException {
        String resourceStr = null;
        try (InputStream is = IOUtils.class.getClassLoader().getResourceAsStream(path)) {
            Scanner s = new Scanner(is).useDelimiter(EOF_REG_EXP);
            resourceStr = s.hasNext() ? s.next() : null;
        }
        return resourceStr;
    }

}
