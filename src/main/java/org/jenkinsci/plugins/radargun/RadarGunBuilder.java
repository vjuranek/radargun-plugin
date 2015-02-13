package org.jenkinsci.plugins.radargun;

import hudson.AbortException;
import hudson.CopyOnWrite;
import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.Util;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.jenkinsci.plugins.radargun.config.NodeSource;
import org.jenkinsci.plugins.radargun.config.ScenarioSource;
import org.jenkinsci.plugins.radargun.config.ScriptSource;
import org.jenkinsci.plugins.radargun.model.MasterScriptConfig;
import org.jenkinsci.plugins.radargun.model.SlaveScriptConfig;
import org.jenkinsci.plugins.radargun.model.impl.MasterShellScript;
import org.jenkinsci.plugins.radargun.model.impl.Node;
import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.model.impl.SlaveShellScript;
import org.jenkinsci.plugins.radargun.util.Resolver;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class RadarGunBuilder extends Builder {

    private static Logger LOGGER = Logger.getLogger(RadarGunBuilder.class.getName());

    private final String radarGunName;
    private final ScenarioSource scenarioSource;
    private final NodeSource nodeSource;
    private final ScriptSource scriptSource;
    private final String log4jConfig;
    private final String defaultJvmArgs;
    private final String workspacePath;
    private final String pluginPath;
    private final String pluginConfigPath;
    private final String reporterPath;

    @DataBoundConstructor
    public RadarGunBuilder(String radarGunName, ScenarioSource scenarioSource, NodeSource nodeSource,
            ScriptSource scriptSource, String log4jConfig, String defaultJvmArgs, String workspacePath,
            String pluginPath, String pluginConfigPath, String reporterPath) {
        this.radarGunName = radarGunName;
        this.scenarioSource = scenarioSource;
        this.nodeSource = nodeSource;
        this.scriptSource = scriptSource;
        this.log4jConfig = Util.fixEmpty(log4jConfig);
        this.defaultJvmArgs = defaultJvmArgs;
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

    public NodeSource getNodeSource() {
        return nodeSource;
    }

    public ScriptSource getScriptSource() {
        return scriptSource;
    }

    public String getLog4jConfig() {
        return log4jConfig;
    }

    public String getDefaultJvmArgs() {
        return defaultJvmArgs;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public String getConfigPath() {
        return pluginConfigPath;
    }

    public String getReporterPath() {
        return reporterPath;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        Resolver resolver = new Resolver(build);
        NodeList nodes = nodeSource.getNodesList(resolver);
        List<NodeRunner> nodeRunners = new ArrayList<NodeRunner>(nodes.getNodeCount());
        RadarGunInstallation rgInstall = getDescriptor().getInstallation(radarGunName);

        // master start script
        RadarGunNodeAction masterAction = new RadarGunNodeAction(build, nodes.getMaster().getHostname(),
                "RadarGun master ");
        build.addAction(masterAction);

        MasterScriptConfig masterConfig = new MasterShellScript();
        masterConfig.withNumberOfSlaves(nodes.getSlaveCount()).withConfigPath(scenarioSource.getTmpScenarioPath(build))
                .withScriptPath(rgInstall.getExecutable(masterConfig, launcher.getChannel()));
        if(pluginPath != null && !pluginPath.isEmpty()) {
            masterConfig.withPlugin(pluginPath);
            if(pluginConfigPath != null && !pluginConfigPath.isEmpty()) {
                masterConfig.withPluginConfig(pluginConfigPath);
            }
        }
        if(reporterPath != null && !reporterPath.isEmpty())
            masterConfig.withReporter(reporterPath);

        String[] masterCmdLine = scriptSource.getMasterCmdLine(build.getWorkspace(), nodes.getMaster().getHostname(),
                masterConfig, buildJvmOptions(build, nodes.getMaster()));

        ProcStarter masterProcStarter = buildProcStarter(build, launcher, masterCmdLine, masterAction.getLogFile());
        nodeRunners.add(new NodeRunner(masterProcStarter, masterAction));

        // slave start scripts
        List<Node> slaves = nodes.getSlaves();
        for (int i = 0; i < slaves.size(); i++) {
            Node slave = slaves.get(i);
            RadarGunNodeAction slaveAction = new RadarGunNodeAction(build, slave.getHostname());
            build.addAction(slaveAction);

            SlaveScriptConfig slaveConfig = new SlaveShellScript();
            slaveConfig.withSlaveIndex(i).withScriptPath(rgInstall.getExecutable(slaveConfig, launcher.getChannel()));
            if(pluginPath != null && !pluginPath.isEmpty()) {
                slaveConfig.withPlugin(pluginPath);
                if(pluginConfigPath != null && !pluginConfigPath.isEmpty()) {
                    slaveConfig.withPluginConfig(pluginConfigPath);
                }
            }

            String[] slaveCmdLine = scriptSource.getSlaveCmdLine(build.getWorkspace(), slave.getHostname(),
                    slaveConfig, buildJvmOptions(build, slave));
            ProcStarter slaveProcStarter = buildProcStarter(build, launcher, slaveCmdLine, slaveAction.getLogFile());
            nodeRunners.add(new NodeRunner(slaveProcStarter, slaveAction));
        }

        // run all start scripts and wait for completion
        // TODO set build to warning if some
        return runRGNodes(nodeRunners);
    }

    private boolean runRGNodes(List<NodeRunner> nodeRunners) throws AbortException {
        int nodeCount = nodeRunners.size();
        CountDownLatch latch = new CountDownLatch(nodeCount);
        ExecutorService executorService = Executors.newFixedThreadPool(nodeCount);

        // submit runners
        List<Future<Integer>> nodeRetCodes = new ArrayList<Future<Integer>>(nodeCount);
        for (NodeRunner runner : nodeRunners) {
            runner.setLatch(latch);
            nodeRetCodes.add(executorService.submit(runner));
        }

        boolean isSuccess = true;
        // wait for processes to be finished
        try {
            latch.await();
            for (Future<Integer> retCode : nodeRetCodes) {
                if (retCode.get() != 0) {
                    isSuccess = false;
                    break;
                }
            }
        } catch (InterruptedException e) {
            LOGGER.log(Level.INFO, "Failing the build - build interrupted", e);
            throw new AbortException(e.getMessage());
        } catch (ExecutionException e) {
            LOGGER.log(Level.INFO, "Failing the build - getting master result has failed", e);
            throw new AbortException(e.getMessage());
        }

        return isSuccess;
    }

    private String buildJvmOptions(AbstractBuild<?, ?> build, Node node) {
        String nodeJvmOpts = Resolver.buildVar(build, node.getJvmOptions());
        String log4jConf = Resolver.buildVar(build, log4jConfig);
        String log4jConfOpt = log4jConf == null ? "" : String.format("%s%s", "-Dlog4j.configuration=", log4jConf);
        String defaultOptsRes = String.format("%s %s", Resolver.buildVar(build, defaultJvmArgs), log4jConfOpt);
        String jvmOpts = nodeJvmOpts == null ? defaultOptsRes : String.format("%s %s", defaultOptsRes, nodeJvmOpts);
        return jvmOpts.trim();
    }

    private ProcStarter buildProcStarter(AbstractBuild<?, ?> build, Launcher launcher, String[] cmdLine, File log)
            throws IOException, InterruptedException {
        BuildListener logListener = new StreamBuildListener(log, Charset.defaultCharset());
        FilePath workspace = workspacePath == null ? build.getWorkspace() : new FilePath(new File(Resolver.buildVar(
                build, workspacePath)));
        ProcStarter procStarter = launcher.launch().cmds(cmdLine).envs(build.getEnvironment(logListener))
                .pwd(workspace).stdout(logListener);
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
