package org.jenkinsci.plugins.radargun.scenario;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractBuild;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * TextScenarioSource
 * 
 * @author vjuranek
 * 
 */
public class TextScenarioSource extends ScenarioSource {

    private String scenario;

    @DataBoundConstructor
    public TextScenarioSource(String scenario) {
        this.scenario = scenario;
    }

    public String getScenario() {
        return scenario;
    }

    public FilePath createTmpScenrioFile(AbstractBuild<?, ?> build) throws InterruptedException, IOException {
        return tmpScenarioFromContent(scenario, build);
    }

    @Extension
    public static class DescriptorImpl extends ScenarioSourceDescriptor {
        public String getDisplayName() {
            return "Text scenario source";
        }
    }

}
