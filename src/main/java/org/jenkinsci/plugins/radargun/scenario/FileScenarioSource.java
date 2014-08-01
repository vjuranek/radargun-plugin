package org.jenkinsci.plugins.radargun.scenario;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractBuild;

import java.io.File;
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
    private transient File tmpScenarioFile;

    @DataBoundConstructor
    public FileScenarioSource(String scenarioPath) {
        this.scenarioPath = scenarioPath;
    }

    public String getScenarioPath() {
        return scenarioPath;
    }

    public String getDefaultScenarioPath() {
        if (tmpScenarioFile != null)
            return tmpScenarioFile.getPath();
        return scenarioPath;
    }

    public void createScenarioFile(AbstractBuild<?, ?> build) throws InterruptedException, IOException {
        FilePath fp = new FilePath(build.getWorkspace(), getScenarioPath());
        String scriptContent = fp.readToString(); // TODO not very safe, if e.g. some malicious user provide path to
                                                  // huge file
        FilePath path = createDefaultScriptFile(scriptContent, build);
        tmpScenarioFile = new File(path.getRemote());
    }

    @Extension
    public static class DescriptorImpl extends ScenarioSourceDescriptor {
        public String getDisplayName() {
            return "File scenario source";
        }
    }

}
