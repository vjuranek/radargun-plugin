package org.jenkinsci.plugins.radargun.model.impl;

public class Node {

    private String hostname;
    private String jvmOptions;


    public Node(String hostname) {
        this.hostname = hostname;
    }

    public Node(String hostname, String jvmOptions) {
        this.hostname = hostname;
        this.jvmOptions = jvmOptions;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    /**
     * 
     * Parse node parameters. First part is node hostname, the rest of the line, separated from hostname by space, are
     * JVM options
     */
    public static Node parseNode(String nodeLine) {
        String[] parts = nodeLine.split(" ", 2);
        return parts.length == 1 ? new Node(parts[0]) : new Node(parts[0], parts[1]);
    }

}
