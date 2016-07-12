package org.jenkinsci.plugins.radargun.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
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

    public static void makeExecutable(String filePath) {
        File msf = new File(filePath);
        msf.setExecutable(true);
    }
    
    public static void checkDeprecatedConfigs(NodeList nodes, ConsoleLogger console) {
        for (Node node : nodes.asList()) {
            if (node.getJvmOptions() != null) {
                console.logAnnot("[RadarGun] WARN: Setting up JVM options via RG jenkins plugin is deprecated and will be removed. Please use RG 3 or higher and set up JVM options direcly in RG!");
            }
        }
    }
    
    public static String[] userCmdsToArray(List<String> userCmds, char cmdSep, boolean sepCharBefore) {
        String cmdSepStr = Character.toString(cmdSep);
        String[] cmds = sepCharBefore ? new String[] {cmdSepStr} : new String[] {};
        for (String cmd : userCmds) {
            cmds = (String[])ArrayUtils.addAll(cmds, cmd.split(" "));
            cmds = (String[])ArrayUtils.add(cmds, cmdSepStr);
        }
        cmds = sepCharBefore ? (String[])ArrayUtils.remove(cmds, cmds.length - 1) : cmds; 
        return cmds;
    }
}
