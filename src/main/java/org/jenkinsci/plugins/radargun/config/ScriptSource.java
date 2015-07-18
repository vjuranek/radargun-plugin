package org.jenkinsci.plugins.radargun.config;

import hudson.DescriptorExtensionList;
import hudson.FilePath;
import hudson.model.Describable;
import hudson.model.Descriptor;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import jenkins.model.Jenkins;

import org.apache.commons.lang.ArrayUtils;
import org.jenkinsci.plugins.radargun.model.MasterScriptConfig;
import org.jenkinsci.plugins.radargun.model.NodeScriptConfig;
import org.jenkinsci.plugins.radargun.model.SlaveScriptConfig;
import org.jenkinsci.plugins.radargun.model.impl.Node;
import org.jenkinsci.plugins.radargun.util.Functions;

/**
 * ScriptSource
 * 
 * @author vjuranek
 * 
 */
public abstract class ScriptSource implements Describable<ScriptSource> {

    public static final char ENV_KEY_VAL_SEPARATOR = '=';
    public static final char ENV_VAR_SEPARATOR = ';';

    public abstract String getMasterScriptPath(FilePath workspace) throws InterruptedException, IOException;

    public abstract String getSlaveScriptPath(FilePath workspace) throws InterruptedException, IOException;

    public abstract void cleanup() throws InterruptedException, IOException;

    public String[] getNodeCmdLine(String nodeScriptPath, Node node, NodeScriptConfig nodeScriptConfig, String jvmOpts,
            String workspace) throws InterruptedException, IOException {
        Functions.makeExecutable(nodeScriptPath);
        // Run with "tail" option ("-t") not to finish immediately once the RG process is started.
        // Otherwise Jenkins finish the process and kill all background thread, i.e. kill RG master.
        // And also to gather the log from master
        nodeScriptConfig.withTailFollow().withWait();
        String envVars = node.getEnvVars() == null ? null : prepareEnvVars(node.getEnvVars());
        String[] remoteExecScriptCmd = envVars == null ? new String[] { nodeScriptPath, node.getHostname() }
                : new String[] { envVars, nodeScriptPath, node.getHostname() };
        String[] remoteScript = (String[]) ArrayUtils.addAll(remoteExecScriptCmd, nodeScriptConfig.getScriptCmd());
        if (jvmOpts != null && !jvmOpts.isEmpty()) {
            remoteScript = (String[]) ArrayUtils.addAll(remoteScript, new String[] { "-J", jvmOpts });
        }
        return remoteScript;
    }

    public String[] getMasterCmdLine(FilePath workspace, Node node, MasterScriptConfig nodeScriptConfig, String jvmOpts)
            throws InterruptedException, IOException {
        String masterScriptPath = getMasterScriptPath(workspace);
        return getNodeCmdLine(masterScriptPath, node, nodeScriptConfig, jvmOpts, workspace.getRemote());
    }

    public String[] getSlaveCmdLine(FilePath workspace, Node node, SlaveScriptConfig nodeScriptConfig, String jvmOpts)
            throws InterruptedException, IOException {
        String slaveScriptPath = getSlaveScriptPath(workspace);
        return getNodeCmdLine(slaveScriptPath, node, nodeScriptConfig, jvmOpts, workspace.getRemote());
    }

    /**
     * 
     * @param envVars
     *            Map o environment variables to be passed to a node when executing command there. This map is expected
     *            not to be null.
     * @return string representation env. variables separated by semicolon
     */
    private String prepareEnvVars(Map<String, String> envVars) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> envIter = envVars.keySet().iterator();
        while (envIter.hasNext()) {
            String key = envIter.next();
            sb.append(key).append(ENV_KEY_VAL_SEPARATOR).append(envVars.get(key)).append(ENV_VAR_SEPARATOR);
        }
        return sb.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Descriptor<ScriptSource> getDescriptor() {
        return Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    public static final DescriptorExtensionList<ScriptSource, Descriptor<ScriptSource>> all() {
        return Jenkins.getInstance().getDescriptorList(ScriptSource.class);
    }

    public static abstract class ScriptSourceDescriptor extends Descriptor<ScriptSource> {
    }

}
