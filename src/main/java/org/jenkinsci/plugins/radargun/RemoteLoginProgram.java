package org.jenkinsci.plugins.radargun;

/**
 * Represents remote login programs like ssh
 * 
 * @author vjuranek
 *
 */
public enum RemoteLoginProgram {
    
    SSH("ssh", new String[] {"/usr/bin/ssh", "-q", "-o", "StrictHostKeyChecking=no"}),
    MRSH("mrsh", new String[] {"/usr/bin/mrsh"});

    
    private final String name;
    private final String[] cmd;
    
    private RemoteLoginProgram(String name, String[] cmd) {
        this.name = name;
        this.cmd = cmd;
    }   

    public String getName() {
        return name;
    }
    
    public String[] getCmd() {
        return cmd;
    }
}
