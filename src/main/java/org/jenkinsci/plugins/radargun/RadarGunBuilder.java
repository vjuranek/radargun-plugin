package org.jenkinsci.plugins.radargun;

import hudson.CopyOnWrite;
import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.model.BuildListener;
import hudson.model.StreamBuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONObject;

import org.jenkinsci.plugins.radargun.config.NodeSource;
import org.jenkinsci.plugins.radargun.config.ScenarioSource;
import org.jenkinsci.plugins.radargun.config.ScriptSource;
import org.jenkinsci.plugins.radargun.model.Node;
import org.jenkinsci.plugins.radargun.model.NodeList;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class RadarGunBuilder extends Builder {

    private final String radarGunName;
    private final ScenarioSource scenarioSource;
    private final NodeSource nodeSource;
    private final ScriptSource scriptSource;
    private final String defaultJvmArgs;

    @DataBoundConstructor
    public RadarGunBuilder(String radarGunName, ScenarioSource scenarioSource, NodeSource nodeSource,
            ScriptSource scriptSource, String defaultJvmArgs) {
        this.radarGunName = radarGunName;
        this.scenarioSource = scenarioSource;
        this.nodeSource = nodeSource;
        this.scriptSource = scriptSource;
        this.defaultJvmArgs = defaultJvmArgs;
    }

    public String getRadarGunName() {
        return radarGunName;
    }

    public ScenarioSource getScenarioSource() {
        return scenarioSource;
    }

    public NodeSource getNodeSource() {
        return nodeSource;
    }

    public ScriptSource getScriptSource() {
        return scriptSource;
    }

    public String getDefaultJvmArgs() {
        return defaultJvmArgs;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        RadarGunInstallation rgInstall = getDescriptor().getInstallation(radarGunName);
        String rgMasterScript = rgInstall.getExecutable(RadarGunExecutable.MASTER, launcher.getChannel());
        String rgSlaveScript = rgInstall.getExecutable(RadarGunExecutable.SLAVE, launcher.getChannel());

        NodeList nodes = nodeSource.getNodesList();
        List<NodeRunner> nodeRunners = new ArrayList<NodeRunner>();

        // master start script
        RadarGunNodeAction masterAction = new RadarGunNodeAction(build, nodes.getMaster().getHostname(),
                "RadarGun master ");
        build.addAction(masterAction);
        String[] masterCmdLine = scriptSource.getMasterCmdLine(nodes.getMaster().getHostname(), rgMasterScript,
                scenarioSource.getTmpScenarioPath(build), Integer.toString(nodes.getSlaveCount()),
                buildJvmOptions(nodes.getMaster()));
        ProcStarter masterProcStarter = buildProcStarter(build, launcher, masterCmdLine, masterAction.getLogFile());
        nodeRunners.add(new NodeRunner(masterProcStarter, masterAction));

        // slave start scripts
        for (Node slave : nodes.getSlaves()) {
            RadarGunNodeAction slaveAction = new RadarGunNodeAction(build, slave.getHostname());
            build.addAction(slaveAction);
            String[] slaveCmdLine = scriptSource.getSlaveCmdLine(slave.getHostname(), rgSlaveScript,
                    buildJvmOptions(slave));
            ProcStarter slaveProcStarter = buildProcStarter(build, launcher, slaveCmdLine, slaveAction.getLogFile());
            nodeRunners.add(new NodeRunner(slaveProcStarter, slaveAction));
        }

        // run all start scripts and wait for completion
        runRGNodes(nodeRunners);

        // TODO correct return value based on return values of scheduled scripts
        return true;
    }

    private void runRGNodes(List<NodeRunner> nodeRunners) {
        CountDownLatch latch = new CountDownLatch(nodeRunners.size());
        ExecutorService executorService = Executors.newFixedThreadPool(nodeRunners.size());
        for (NodeRunner runner : nodeRunners) {
            runner.setLatch(latch);
            executorService.submit(runner);
        }
        // wait for processes to be finished
        try {
            latch.await();
        } catch (InterruptedException e) {
            // TODO log exception
        }
    }

    private String buildJvmOptions(Node node) {
        return node.getJvmOptions() == null ? defaultJvmArgs : String.format("%s %s", defaultJvmArgs,
                node.getJvmOptions());
    }

    private ProcStarter buildProcStarter(AbstractBuild<?, ?> build, Launcher launcher, String[] cmdLine, File log)
            throws IOException, InterruptedException {
        BuildListener logListener = new StreamBuildListener(log, Charset.defaultCharset());
        ProcStarter procStarter = launcher.launch().cmds(cmdLine).envs(build.getEnvironment(logListener))
                .pwd(build.getWorkspace()).stdout(logListener);
        return procStarter;
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

        public static DescriptorExtensionList<NodeSource, Descriptor<NodeSource>> getNodeSources() {
            return NodeSource.all();
        }

        public static DescriptorExtensionList<ScriptSource, Descriptor<ScriptSource>> getScriptSources() {
            return ScriptSource.all();
        }

    }
}
