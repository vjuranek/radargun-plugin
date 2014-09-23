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

    @DataBoundConstructor
    public TextScriptSource(String masterScript, String slaveScript) {
        this.masterScript = masterScript;
        this.slaveScript = slaveScript;
    }

    public String getMasterScript() {
        return masterScript;
    }

    public String getSlaveScript() {
        return slaveScript;
    }

    @Override
    public String getMasterScriptPath(FilePath workspace) throws InterruptedException, IOException {
        FilePath master = workspace.createTextTempFile("radargun_master", ".sh", masterScript, true);
        return master.getRemote();
    }

    @Override
    public String getSlaveScriptPath(FilePath workspace) throws InterruptedException, IOException {
        FilePath slave = workspace.createTextTempFile("radargun_slave", ".sh", slaveScript, true);
        return slave.getRemote();
    }

    @Extension
    public static class DescriptorImpl extends ScriptSourceDescriptor {
        public String getDisplayName() {
            return "Text script source";
        }
    }

}
