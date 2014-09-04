package org.jenkinsci.plugins.radargun.config;

import hudson.DescriptorExtensionList;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

/**
 * ScriptSource
 * 
 * @author vjuranek
 * 
 */
public abstract class ScriptSource implements Describable<ScriptSource> {

    public abstract String getMasterScriptPath();

    public abstract String getSlaveScriptPath();

    /**
     * Prepares command line which will be used to start master process
     * 
     * @param hostname
     *            master hostname (typically for ssh there)
     * @param rgMasterScript
     *            path to RG master.sh script on this machine
     * @param scenarioPath
     *            path to scenario to be executed
     * @param jvmOpts
     *            additional JVM options for master process
     * 
     */
    public String[] getMasterCmdLine(String hostname, String rgMasterScript, String scenarioPath, String slaveNumber,
            String jvmOpts) {
        // Run with "tail" option ("-t") not to finish immediately once the RG process is started.
        // Otherwise Jenkins finish the process and kill all background thread, i.e. kill RG master.
        // And also to gather the log from master
        return new String[] { getMasterScriptPath(), hostname, rgMasterScript , "-t", scenarioPath, slaveNumber, jvmOpts };
    }

    /**
     * Prepares command line which will be used to start slave process
     * 
     * @param hostname
     *            slave hostname (typically for ssh there)
     * @param rgSlaveScript
     *            path to RG slave.sh script on this machine
     * @param slaveIndex
     *            slave index
     * @param jvmOpts
     *            additional JVM options for master process           
     * 
     */
    public String[] getSlaveCmdLine(String hostname, String rgSlaveScript, String slaveIndex, String jvmOpts) {
        // run with "tail" option ("-t") to gather the logs from slaves
        return new String[] { getSlaveScriptPath(), hostname, rgSlaveScript, "-t", slaveIndex, jvmOpts };
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
