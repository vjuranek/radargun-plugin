package org.jenkinsci.plugins.radargun.config;

import hudson.DescriptorExtensionList;
import hudson.FilePath;
import hudson.model.Describable;
import hudson.model.Descriptor;

import java.io.IOException;

import jenkins.model.Jenkins;

import org.apache.commons.lang.ArrayUtils;
import org.jenkinsci.plugins.radargun.model.MasterScriptConfig;
import org.jenkinsci.plugins.radargun.model.NodeScriptConfig;
import org.jenkinsci.plugins.radargun.model.SlaveScriptConfig;
import org.jenkinsci.plugins.radargun.util.Functions;

/**
 * ScriptSource
 * 
 * @author vjuranek
 * 
 */
public abstract class ScriptSource implements Describable<ScriptSource> {

    public abstract String getMasterScriptPath(FilePath workspace) throws InterruptedException, IOException;

    public abstract String getSlaveScriptPath(FilePath workspace) throws InterruptedException, IOException;
    
    public abstract void cleanup() throws InterruptedException, IOException;

    public String[] getNodeCmdLine(String nodeScriptPath, String hostname, NodeScriptConfig nodeScriptConfig, String jvmOpts, String workspace) throws InterruptedException, IOException {
        Functions.makeExecutable(nodeScriptPath);
        // Run with "tail" option ("-t") not to finish immediately once the RG process is started.
        // Otherwise Jenkins finish the process and kill all background thread, i.e. kill RG master.
        // And also to gather the log from master
        nodeScriptConfig.withTailFollow().withWait();
        String[] remoteExecScriptCmd =  new String[] { nodeScriptPath, hostname, "cd", workspace+";"}; //TODO override by workspace from Jenkins config
        String[] remoteScript = (String[])ArrayUtils.addAll(remoteExecScriptCmd, nodeScriptConfig.getScriptCmd());
        if(jvmOpts != null && !jvmOpts.isEmpty()) {
            remoteScript = (String[]) ArrayUtils.addAll(remoteScript, new String[] {"-J", jvmOpts});
        }
        return remoteScript;
    }
    
    public String[] getMasterCmdLine(FilePath workspace, String hostname, MasterScriptConfig nodeScriptConfig, String jvmOpts) throws InterruptedException, IOException {
        String masterScriptPath = getMasterScriptPath(workspace);
        return getNodeCmdLine(masterScriptPath, hostname, nodeScriptConfig, jvmOpts, workspace.getRemote());
    }
    
    public String[] getSlaveCmdLine(FilePath workspace, String hostname, SlaveScriptConfig nodeScriptConfig, String jvmOpts) throws InterruptedException, IOException {
        String slaveScriptPath = getSlaveScriptPath(workspace);
        return getNodeCmdLine(slaveScriptPath, hostname, nodeScriptConfig, jvmOpts, workspace.getRemote());
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
