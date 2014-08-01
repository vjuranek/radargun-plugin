package org.jenkinsci.plugins.radargun.scenario;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractBuild;

import java.io.File;
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
    private transient File tmpScenarioFile;

    @DataBoundConstructor
    public TextScenarioSource(String scenario) {
        this.scenario = scenario;
    }

    public String getScenario() {
        return scenario;
    }

    public void createScriptFile(AbstractBuild<?, ?> build) throws InterruptedException, IOException {
        FilePath path = createDefaultScriptFile(scenario, build);
        tmpScenarioFile = new File(path.getRemote());
    }

    public String getDefaultScriptPath() {
        return tmpScenarioFile.getPath();
    }

    @Extension
    public static class DescriptorImpl extends ScenarioSourceDescriptor {
        public String getDisplayName() {
            return "Text scenario source";
        }
    }

}
