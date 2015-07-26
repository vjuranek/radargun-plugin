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
 * Init scripts used for launching RG on remote nodes, typically via ssh. This seems to be redundant and will be removed
 * soon.
 * 
 * TODO replace by more convenient construct or script should be completely generated based on used configuration.
 * 
 * @author vjuranek
 * 
 */
@Deprecated
public abstract class ScriptSource implements Describable<ScriptSource> {

    public static final String CD_CMD = "cd ";
    public static final String JAVA_PROP_PREFIX = "-D";
    public static final String ENV_CMD = "env ";
    public static final String EXPORT_CMD = "export ";
    public static final char ENV_KEY_VAL_SEPARATOR = '=';
    public static final char ENV_VAR_QUOTE = '"';
    public static final char VAR_SEPARATOR = ' ';
    public static final char CMD_SEPARATOR = ';';

    public abstract String getMasterScriptPath(FilePath workspace) throws InterruptedException, IOException;

    public abstract String getSlaveScriptPath(FilePath workspace) throws InterruptedException, IOException;

    public abstract void cleanup() throws InterruptedException, IOException;

    public String[] getNodeCmdLine(String nodeScriptPath, Node node, NodeScriptConfig nodeScriptConfig, String workspace)
            throws InterruptedException, IOException {
        Functions.makeExecutable(nodeScriptPath);
        // Run with "tail" option ("-t") not to finish immediately once the RG process is started.
        // Otherwise Jenkins finish the process and kill all background thread, i.e. kill RG master.
        // And also to gather the log from master
        nodeScriptConfig.withTailFollow().withWait();
        String envVars = node.getEnvVars() == null ? null : prepareEnvVars(node.getEnvVars());
        String[] remoteExecScriptCmd = envVars == null ? new String[] { nodeScriptPath, node.getHostname(), CD_CMD,
                workspace + CMD_SEPARATOR } : new String[] { nodeScriptPath, node.getHostname(), CD_CMD,
                workspace + CMD_SEPARATOR, ENV_CMD, envVars };
        String[] remoteScript = (String[]) ArrayUtils.addAll(remoteExecScriptCmd, nodeScriptConfig.getScriptCmd());
        return remoteScript;
    }

    public String[] getMasterCmdLine(FilePath workspace, Node node, MasterScriptConfig nodeScriptConfig)
            throws InterruptedException, IOException {
        String masterScriptPath = getMasterScriptPath(workspace);
        return getNodeCmdLine(masterScriptPath, node, nodeScriptConfig, workspace.getRemote());
    }

    public String[] getSlaveCmdLine(FilePath workspace, Node node, SlaveScriptConfig nodeScriptConfig)
            throws InterruptedException, IOException {
        String slaveScriptPath = getSlaveScriptPath(workspace);
        return getNodeCmdLine(slaveScriptPath, node, nodeScriptConfig, workspace.getRemote());
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
            sb.append(key).append(ENV_KEY_VAL_SEPARATOR).append(ENV_VAR_QUOTE).append(envVars.get(key))
                    .append(ENV_VAR_QUOTE).append(VAR_SEPARATOR);
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
