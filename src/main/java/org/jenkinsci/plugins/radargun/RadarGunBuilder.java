package org.jenkinsci.plugins.radargun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.radargun.config.NodeConfigSource;
import org.jenkinsci.plugins.radargun.config.ScenarioSource;
import org.jenkinsci.plugins.radargun.config.ScriptSource;
import org.jenkinsci.plugins.radargun.model.RgProcess;
import org.jenkinsci.plugins.radargun.model.impl.Node;
import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.model.impl.RgMasterProcessImpl;
import org.jenkinsci.plugins.radargun.model.impl.RgSlaveProcessImpl;
import org.jenkinsci.plugins.radargun.util.ConsoleLogger;
import org.jenkinsci.plugins.radargun.util.Functions;
import org.jenkinsci.plugins.radargun.util.Resolver;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.AbortException;
import hudson.CopyOnWrite;
import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;

public class RadarGunBuilder extends Builder {

    private static Logger LOGGER = Logger.getLogger(RadarGunBuilder.class.getName());

    private final String radarGunName;
    private final ScenarioSource scenarioSource;
    private final NodeConfigSource nodeSource;
    private final ScriptSource scriptSource;
    private final String workspacePath;
    private final String pluginPath;
    private final String pluginConfigPath;
    private final String reporterPath;

    @DataBoundConstructor
    public RadarGunBuilder(String radarGunName, ScenarioSource scenarioSource, NodeConfigSource nodeSource,
            ScriptSource scriptSource, String workspacePath, String pluginPath, String pluginConfigPath,
            String reporterPath) {
        this.radarGunName = radarGunName;
        this.scenarioSource = scenarioSource;
        this.nodeSource = nodeSource;
        this.scriptSource = scriptSource;
        this.workspacePath = Util.fixEmpty(workspacePath);
        this.pluginPath = pluginPath;
        this.pluginConfigPath = pluginConfigPath;
        this.reporterPath = reporterPath;
    }

    public String getRadarGunName() {
        return radarGunName;
    }

    public ScenarioSource getScenarioSource() {
        return scenarioSource;
    }

    public NodeConfigSource getNodeSource() {
        return nodeSource;
    }

    public ScriptSource getScriptSource() {
        return scriptSource;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public String getPluginConfigPath() {
        return pluginConfigPath;
    }

    public String getReporterPath() {
        return reporterPath;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        Resolver.init(build);
        ConsoleLogger console = new ConsoleLogger(listener);
        
        RadarGunInstallation rgInstall = getDescriptor().getInstallation(radarGunName);
        build.addAction(new RadarGunInvisibleAction(rgInstall.getHome()));

        NodeList nodes = nodeSource.getNodesList();

        //check deprecated options
        Functions.checkDeprecatedConfigs(nodes, console);
        
        RgBuild rgBuild = new RgBuild(this, build, launcher, nodes, rgInstall);
        try {
            return runRgProcesses(prepareRgProcesses(rgBuild));
        } catch (Exception e) {
            console.logAnnot("[RadarGun] ERROR: something went wrong, caught exception: " + e.getMessage());
            e.printStackTrace(console.getLogger());
            return false;
        } finally {
            scriptSource.cleanup();
            scenarioSource.cleanup();
        }
    }

    private List<RgProcess> prepareRgProcesses(RgBuild rgBuild) {
        List<RgProcess> rgProcesses = new ArrayList<RgProcess>(rgBuild.getNodes().getNodeCount());
        rgProcesses.add(new RgMasterProcessImpl(rgBuild));
        List<Node> slaves = rgBuild.getNodes().getSlaves();
        for (int i = 0; i < slaves.size(); i++) {
            rgProcesses.add(new RgSlaveProcessImpl(rgBuild, i));
        } 
        return rgProcesses;
    }
    
    private boolean runRgProcesses(List<RgProcess> rgProcesses) throws AbortException {
        ExecutorService executorService = Executors.newFixedThreadPool(rgProcesses.size());

        // start processes
        for (RgProcess process : rgProcesses) {
            process.start(executorService);
        }

        boolean isSuccess = false;
        try {
            // wait for master process to be finished, failure of the slave process should be detected by RG master
            isSuccess = rgProcesses.get(0).waitForResult() == 0;
        } catch (InterruptedException e) {
            //TODO actually shouln't fail the build but set it to canceled
            LOGGER.log(Level.INFO, "Stopping the build - build interrupted", e);
            //throw new AbortException(e.getMessage());
        } catch (ExecutionException e) {
            LOGGER.log(Level.INFO, "Failing the build - getting master result has failed", e);
            throw new AbortException(e.getMessage());
        } finally {
            List<Runnable> notStarted = executorService.shutdownNow();
            LOGGER.log(Level.FINE, "Number of tasks that weren't started: " + notStarted.size());
        }

        return isSuccess;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @CopyOnWrite
        private volatile List<RadarGunInstallation> installations = new ArrayList<RadarGunInstallation>();

        public DescriptorImpl() {
            load();
        }

        public List<RadarGunInstallation> getInstallations() {
            return installations;
        }

        public void setInstallations(RadarGunInstallation... installations) {
            this.installations = new ArrayList<RadarGunInstallation>();
            for (RadarGunInstallation installation : installations) {
                this.installations.add(installation);
            }
        }

        public RadarGunInstallation getInstallation(String installationName) {
            if (installationName == null || installationName.isEmpty())
                return null;

            for (RadarGunInstallation i : installations) {
                if (i.getName().equals(installationName))
                    return i;
            }
            return null;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Run RadarGun";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }

        public ListBoxModel doFillRadarGunNameItems() {
            ListBoxModel lb = new ListBoxModel();
            for (RadarGunInstallation rgi : installations) {
                lb.add(rgi.getName(), rgi.getName());
            }
            return lb;
        }

        public static DescriptorExtensionList<ScenarioSource, Descriptor<ScenarioSource>> getScenarioSources() {
            return ScenarioSource.all();
        }

        public static DescriptorExtensionList<NodeConfigSource, Descriptor<NodeConfigSource>> getNodeSources() {
            return NodeConfigSource.all();
        }

        public static DescriptorExtensionList<ScriptSource, Descriptor<ScriptSource>> getScriptSources() {
            return ScriptSource.all();
        }

    }
}
