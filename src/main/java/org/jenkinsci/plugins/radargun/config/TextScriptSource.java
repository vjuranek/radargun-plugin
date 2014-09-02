package org.jenkinsci.plugins.radargun.config;

import hudson.Extension;

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
    
    public String getMasterScriptPath() {
        return null;
    }
    
    public String getSlaveScriptPath() {
        return null;
    }
    
    @Extension
    public static class DescriptorImpl extends ScriptSourceDescriptor {
        public String getDisplayName() {
            return "Text script source";
        }
    }

}
