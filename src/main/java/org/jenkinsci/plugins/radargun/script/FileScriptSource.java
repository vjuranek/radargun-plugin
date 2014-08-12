package org.jenkinsci.plugins.radargun.script;

import hudson.Extension;

import java.io.File;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * FileScriptSource
 * 
 * @author vjuranek
 * 
 */
public class FileScriptSource extends ScriptSource {

    private final String masterScriptPath;
    private final String slaveScriptPath;
    private transient File tmpScriptFile;

    @DataBoundConstructor
    public FileScriptSource(String masterScriptPath, String slaveScriptPath) {
        this.masterScriptPath = masterScriptPath;
        this.slaveScriptPath = slaveScriptPath;
    }

    public String getMasterScriptPath() {
        return masterScriptPath;
    }
    
    public String getSlaveScriptPath() {
        return slaveScriptPath;
    }

    public String getDefaultScriptPath() {
        if (tmpScriptFile != null)
            return tmpScriptFile.getPath();
        return masterScriptPath;
    }

    @Extension
    public static class DescriptorImpl extends ScriptSourceDescriptor {
        public String getDisplayName() {
            return "File script source";
        }
    }

}
