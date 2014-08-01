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

    private String scriptPath;
    private transient File tmpScriptFile;

    @DataBoundConstructor
    public FileScriptSource(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public String getDefaultScriptPath() {
        if (tmpScriptFile != null)
            return tmpScriptFile.getPath();
        return scriptPath;
    }

    @Extension
    public static class DescriptorImpl extends ScriptSourceDescriptor {
        public String getDisplayName() {
            return "File script source";
        }
    }

}
