package org.jenkinsci.plugins.radargun.model.impl;

import java.util.Map;

/**
 * Represents RG master node and keeps it's configuration.
 * Currently not special configuration for master is needed.
 * Kept as a marker class.
 * 
 * @author vjuranek
 *
 */
public class MasterNode extends Node {
    
    public MasterNode(Node node) {
        super(node.getName(), node.getFqdn(), node.getJvmOptions(), node.getJavaProps(), node.getEnvVars());
    }
    
    public MasterNode(String name, String fqdn) {
        super(name, fqdn);
    }

    public MasterNode(String hostname, String fqdn, String jvmOptions, Map<String, String> javaProps, Map<String, String> envVars) {
        super(hostname, fqdn, jvmOptions, javaProps, envVars);
    }

}
