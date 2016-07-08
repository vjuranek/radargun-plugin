package org.jenkinsci.plugins.radargun.util;

import java.io.File;
import java.io.IOException;

import org.jenkinsci.plugins.radargun.model.impl.Node;
import org.jenkinsci.plugins.radargun.model.impl.NodeList;

import hudson.FilePath;
import hudson.model.BuildListener;

/**
 * 
 * @author vjuranek
 * 
 */
public class Functions {

    public static String convertWsToCanonicalPath(FilePath workspace) {
        String workspacePath = "";
        try {
            workspacePath = (new File(workspace.toURI())).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return workspacePath;
    }

    public static String cmdArrayToString(String[] cmds) {
        StringBuilder buf = new StringBuilder();
        for (String c : cmds) {
            buf.append(c).append(" ");
        }
        return buf.substring(0, buf.length() - 1);
    }

    public static void makeExecutable(String filePath) {
        File msf = new File(filePath);
        msf.setExecutable(true);
    }
    
    public static void checkDeprecatedConfigs(NodeList nodes, BuildListener listener) {
        for (Node node : nodes.asList()) {
            if (node.getJvmOptions() != null) {
                listener.getLogger().println("Setting up JVM options via RG jenkins plugin is deprecated and will be removed. Please use RG 3 or higher and set up JVM options direcly in RG!");
            }
        }
    }
}
