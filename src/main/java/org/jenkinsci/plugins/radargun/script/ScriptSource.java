package org.jenkinsci.plugins.radargun.script;

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
