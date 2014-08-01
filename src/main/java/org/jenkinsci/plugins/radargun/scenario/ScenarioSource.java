package org.jenkinsci.plugins.radargun.scenario;

import java.io.IOException;

import hudson.DescriptorExtensionList;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.ParametersAction;
import jenkins.model.Jenkins;

/**
 * ScenarioSource
 * 
 * @author vjuranek
 *
 */
public abstract class ScenarioSource implements Describable<ScenarioSource> {
    
    protected static final String DEFAULT_SCENARIO_NAME = "radarGunScenario";
    protected static final String DEFAULT_SCENARIO_SUFFIX = ".xml";
    
    
    /**
     * Replace parameters in scenario and stores scenario into tmp file in workspace
     */
    public FilePath createDefaultScriptFile(String scenarioContent, AbstractBuild<?,?> build) throws InterruptedException, IOException {
        String script = scenarioContent;
        ParametersAction pa = build.getAction(ParametersAction.class);
        if(pa != null)
            script = pa.substitute(build, script);
        //TODO env. var expansion? Expand on node where it will be launched
        FilePath path = build.getWorkspace().createTextTempFile(DEFAULT_SCENARIO_NAME, DEFAULT_SCENARIO_SUFFIX, script, true);
        return path;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Descriptor<ScenarioSource> getDescriptor() {
        return Jenkins.getInstance().getDescriptorOrDie(getClass());
    }
    
    public static final DescriptorExtensionList<ScenarioSource, Descriptor<ScenarioSource>> all() {
        return Jenkins.getInstance().getDescriptorList(ScenarioSource.class);
    }
    
    public static abstract class ScenarioSourceDescriptor extends Descriptor<ScenarioSource> {
    }

}
