package org.jenkinsci.plugins.radargun.util;

import hudson.FilePath;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author vjuranek
 *
 */
public class Functions {

    public static String convertWsToCanonicalPath(FilePath workspace){
        String workspacePath = "";
        try {
            workspacePath = (new File(workspace.toURI())).getCanonicalPath();
        } catch(IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return workspacePath;
    }
    
    public static String cmdArrayToString(String[] cmds){
        StringBuilder buf = new StringBuilder();
        for (String c : cmds) {
            buf.append(c).append(" ");
        }
        return buf.substring(0, buf.length()-1);
    }
}
