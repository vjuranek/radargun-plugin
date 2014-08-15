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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONObject;

import org.jenkinsci.plugins.radargun.model.Node;
import org.jenkinsci.plugins.radargun.model.NodeList;
import org.jenkinsci.plugins.radargun.scenario.ScenarioSource;
import org.jenkinsci.plugins.radargun.script.ScriptSource;
import org.jenkinsci.plugins.radargun.util.ParseUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class RadarGunBuilder extends Builder {

    private final String radarGunName;
    private final ScenarioSource scenarioSource;
    private final String nodeListString;
    private final ScriptSource scriptSource;
    private final String defaultJvmArgs;

    @DataBoundConstructor
    public RadarGunBuilder(String radarGunName, ScenarioSource scenarioSource, String nodeListString,
            ScriptSource scriptSource, String defaultJvmArgs) {
        this.radarGunName = radarGunName;
        this.scenarioSource = scenarioSource;
        this.nodeListString = nodeListString;
        this.scriptSource = scriptSource;
        this.defaultJvmArgs = defaultJvmArgs;
    }

    public String getRadarGunName() {
        return radarGunName;
    }

    public ScenarioSource getScenarioSource() {
        return scenarioSource;
    }

    public String getNodeListString() {
        return nodeListString;
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

        NodeList nodes = ParseUtils.parseNodeList(nodeListString);

        RadarGunInstallation rgInstall = getDescriptor().getInstallation(radarGunName);
        // TODO check for null rgInstall
        // String script = rgInstall.getExecutable(RadarGunExecutable.LOCAL, launcher.getChannel());

        // master start script
        String[] masterCmdLine = scriptSource.getMasterCmdLine(nodes.getMaster().getHostname(),
                scenarioSource.getScenarioPath(), buildJvmOptions(nodes.getMaster()));
        //TODO log name
        ProcStarter masterProcStarter = buildProcStarter(build, launcher, masterCmdLine, "master.log");
        
        // slave start script
        List<ProcStarter> slaveProcStarters = new ArrayList<>();
        for (Node slave : nodes.getSlaves()) {
            String[] slaveCmdLine = scriptSource.getSlaveCmdLine(slave.getHostname(), buildJvmOptions(slave));
            //TODO log name
            slaveProcStarters.add(buildProcStarter(build, launcher, slaveCmdLine, String.format("slave_%s.log", slave.getHostname())));
        }

        // run all start scripts and wait for completion
        runRGNodes(masterProcStarter, slaveProcStarters);

        // TODO correct return value based on return values of scheduled scripts
        return true;
    }

    private void runRGNodes(ProcStarter masterProcStarter, List<ProcStarter> slaveProcStarters) {
        int nodeCount = slaveProcStarters.size() + 1;
        CountDownLatch latch = new CountDownLatch(nodeCount);
        ExecutorService executorService = Executors.newFixedThreadPool(nodeCount);
        // schedule master run
        executorService.submit(new NodeRunner(masterProcStarter, latch));
        // schedule slave runs
        for (ProcStarter slaveProcStarter : slaveProcStarters) {
            executorService.submit(new NodeRunner(slaveProcStarter, latch));
        }
        // wait for processes to be finished
        try {
            latch.await();
        } catch (InterruptedException e) {
            // TODO log exception
        }
    }

    private String buildJvmOptions(Node node) {
        return node.getJvmOptions() == null ? defaultJvmArgs : String.format("%s %s", defaultJvmArgs, node.getJvmOptions());
    }

    private ProcStarter buildProcStarter(AbstractBuild<?, ?> build, Launcher launcher, String[] cmdLine, String logFileName)
            throws IOException, InterruptedException {
        BuildListener log = new StreamBuildListener(new PrintStream(new FileOutputStream(logFileName)),
                Charset.defaultCharset());
        ProcStarter procStarter = launcher.launch().cmds(cmdLine).envs(build.getEnvironment(log))
                .pwd(build.getWorkspace()).stdout(log);
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

        public static DescriptorExtensionList<ScriptSource, Descriptor<ScriptSource>> getScriptSources() {
            return ScriptSource.all();
        }

    }
}
