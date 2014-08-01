package org.jenkinsci.plugins.radargun.script;

import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * TextScriptSource
 * 
 * @author vjuranek
 * 
 */
public class TextScriptSource extends ScriptSource {

    private String script;

    @DataBoundConstructor
    public TextScriptSource(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    @Extension
    public static class DescriptorImpl extends ScriptSourceDescriptor {
        public String getDisplayName() {
            return "Text script source";
        }
    }

}
