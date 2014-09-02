package org.jenkinsci.plugins.radargun.config;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * FileScriptSource
 * 
 * @author vjuranek
 * 
 */
public class FileScriptSource extends ScriptSource {

    private final String mastertPath;
    private final String slavePath;

    @DataBoundConstructor
    public FileScriptSource(String masterPath, String slavePath) {
        this.mastertPath = masterPath;
        this.slavePath = slavePath;
    }

    public String getMasterPath() {
        return mastertPath;
    }
    
    public String getSlavePath() {
        return slavePath;
    }
    
    public String getMasterScriptPath() {
        return mastertPath;
    }
    
    public String getSlaveScriptPath() {
        return slavePath;
    }

    @Extension
    public static class DescriptorImpl extends ScriptSourceDescriptor {
        public String getDisplayName() {
            return "File script source";
        }
    }

}
