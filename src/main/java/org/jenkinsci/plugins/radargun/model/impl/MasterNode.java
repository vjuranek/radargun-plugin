package org.jenkinsci.plugins.radargun.model.impl;

import java.util.Map;

/**
 * Represents RG master node and keeps it's configuration.
 * 
 * @author vjuranek
 *
 */
public class MasterNode extends Node {
    
    private final String fqdn;
    
    public MasterNode(Node node, String fqdn) {
        super(node.getHostname(), node.getJvmOptions(), node.getJavaProps(), node.getEnvVars());
        this.fqdn = fqdn;
    }
    
    public MasterNode(String hostname, String fqdn) {
        super(hostname);
        this.fqdn = fqdn;
    }

    public MasterNode(String hostname, String jvmOptions, Map<String, String> javaProps, Map<String, String> envVars, String fqdn) {
        super(hostname, jvmOptions, javaProps, envVars);
        this.fqdn = fqdn;
    }
    
    public String getFqdn() {
        return fqdn;
    }

}
