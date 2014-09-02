package org.jenkinsci.plugins.radargun.config;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractBuild;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * FileScenarioSource
 * 
 * @author vjuranek
 * 
 */
public class FileScenarioSource extends ScenarioSource {

    private String scenarioPath;

    @DataBoundConstructor
    public FileScenarioSource(String scenarioPath) {
        this.scenarioPath = scenarioPath;
    }

    public String getScenarioPath() {
        return scenarioPath;
    }

    public FilePath createTmpScenrioFile(AbstractBuild<?, ?> build) throws InterruptedException, IOException {
        return tmpScenarioFromFile(scenarioPath, build);
    }

    @Extension
    public static class DescriptorImpl extends ScenarioSourceDescriptor {
        public String getDisplayName() {
            return "File scenario source";
        }
    }

}
