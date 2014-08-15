package org.jenkinsci.plugins.radargun.scenario;

import hudson.DescriptorExtensionList;
import hudson.FilePath;
import hudson.model.Describable;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;

import java.io.IOException;

import jenkins.model.Jenkins;

import org.jenkinsci.plugins.radargun.util.Resolver;

/**
 * ScenarioSource
 * 
 * @author vjuranek
 * 
 */
public abstract class ScenarioSource implements Describable<ScenarioSource> {

    protected static final String DEFAULT_SCENARIO_NAME = "radarGunScenario";
    protected static final String DEFAULT_SCENARIO_SUFFIX = ".xml";

    private transient String tmpScenarioPath;

    public abstract FilePath createTmpScenrioFile(AbstractBuild<?, ?> build) throws InterruptedException, IOException;

    public String getTmpScenarioPath(AbstractBuild<?, ?> build) throws InterruptedException, IOException {
        if (tmpScenarioPath == null) {
            FilePath tmpScenario = createTmpScenrioFile(build);
            tmpScenarioPath = tmpScenario.getRemote();
        }
        return tmpScenarioPath;
    }

    /**
     * Replace parameters in scenario and stores scenario into tmp file in workspace
     */
    public FilePath tmpScenarioFromContent(String scenarioContent, AbstractBuild<?, ?> build)
            throws InterruptedException, IOException {
        String scenario = Resolver.buildVar(build, scenarioContent);
        // TODO env. var expansion? Expand on node where it will be launched
        FilePath path = build.getWorkspace().createTextTempFile(DEFAULT_SCENARIO_NAME, DEFAULT_SCENARIO_SUFFIX,
                scenario, true);
        return path;
    }

    public FilePath tmpScenarioFromFile(String scenarioPath, AbstractBuild<?, ?> build) throws InterruptedException,
            IOException {
        String path = Resolver.buildVar(build, scenarioPath);
        FilePath fp = new FilePath(build.getWorkspace(), path);
        String scenarioContent = fp.readToString(); // TODO not very safe, if e.g. some malicious user provide path to
                                                    // huge file
        // TODO env. var expansion? Expand on node where it will be launched
        return tmpScenarioFromContent(scenarioContent, build);
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
