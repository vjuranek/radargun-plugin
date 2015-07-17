package org.jenkinsci.plugins.radargun.model.impl;

import java.util.Map;

public class Node {

    public static final String JVM_OPTS_KEY = "jvmOpts";
    public static final String ENV_VARS_KEY = "envVars";
    
    private String hostname;
    private String jvmOptions;
    private Map<String, String> envVars;

    public Node(String hostname) {
        this.hostname = hostname;
    }

    public Node(String hostname, String jvmOptions, Map<String, String> envVars) {
        this.hostname = hostname;
        this.jvmOptions = jvmOptions;
        this.envVars = envVars;
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

    public Map<String, String> getEnvVars() {
        return envVars;
    }

    public void setEnvVars(Map<String, String> envVars) {
        this.envVars = envVars;
    }

    public static Node parseNode(String hostname, Map<String, Object> nodeConfig) {
        String jvmOpts = nodeConfig.containsKey(JVM_OPTS_KEY) ? (String)nodeConfig.get(JVM_OPTS_KEY) : null;
        @SuppressWarnings("unchecked")
        Map<String, String> envVars = nodeConfig.containsKey(ENV_VARS_KEY) ? (Map<String, String>)nodeConfig.get(ENV_VARS_KEY) : null;
        return new Node(hostname, jvmOpts, envVars);
    }

}
