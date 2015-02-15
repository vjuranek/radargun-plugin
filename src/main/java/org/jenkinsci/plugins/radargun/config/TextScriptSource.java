package org.jenkinsci.plugins.radargun.config;

import hudson.Extension;
import hudson.FilePath;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * TextScriptSource
 * 
 * @author vjuranek
 * 
 */
public class TextScriptSource extends ScriptSource {

    private final String masterScript;
    private final String slaveScript;
    
    private transient FilePath masterScriptPath;
    private transient FilePath slaveScriptPath;

    @DataBoundConstructor
    public TextScriptSource(String masterScript, String slaveScript) {
        this.masterScript = masterScript;
        this.slaveScript = slaveScript;
        masterScriptPath = null;
        slaveScriptPath = null;
    }

    public String getMasterScript() {
        return masterScript;
    }

    public String getSlaveScript() {
        return slaveScript;
    }
    
    @Override
    public void cleanup() throws InterruptedException, IOException {
        try {
            if(masterScriptPath != null)
                masterScriptPath.delete();
            if(slaveScriptPath != null)
                slaveScriptPath.delete();
        } finally {
            masterScriptPath = null;
            slaveScriptPath = null;
        }
    }

    @Override
    public String getMasterScriptPath(FilePath workspace) throws InterruptedException, IOException {
        if(masterScriptPath == null)
            masterScriptPath = createMasterScriptFile(workspace);
        return masterScriptPath.getRemote();
    }

    @Override
    public String getSlaveScriptPath(FilePath workspace) throws InterruptedException, IOException {
        if(slaveScriptPath == null)
            slaveScriptPath = createSlaveScriptPath(workspace); 
        return slaveScriptPath.getRemote();
    }
    
    private FilePath createMasterScriptFile(FilePath workspace) throws InterruptedException, IOException {
        return workspace.createTextTempFile("radargun_master", ".sh", masterScript, true);
    }
    
    private FilePath createSlaveScriptPath(FilePath workspace) throws InterruptedException, IOException {
        return workspace.createTextTempFile("radargun_slave", ".sh", slaveScript, true);
    }

    @Extension
    public static class DescriptorImpl extends ScriptSourceDescriptor {
        public String getDisplayName() {
            return "Text script source";
        }
    }

}
