package org.jenkinsci.plugins.radargun.model.impl;

import java.io.IOException;

import org.jenkinsci.plugins.radargun.NodeRunner;
import org.jenkinsci.plugins.radargun.RadarGunNodeAction;
import org.jenkinsci.plugins.radargun.RgBuild;
import org.jenkinsci.plugins.radargun.model.MasterScriptConfig;
import org.jenkinsci.plugins.radargun.model.RgMasterProcess;
import org.jenkinsci.plugins.radargun.util.Functions;
import org.jenkinsci.plugins.radargun.util.Resolver;

import hudson.Launcher.ProcStarter;

public class RgMasterProcessImpl extends AbstractRgProcess implements RgMasterProcess {

    private final RgBuild rgBuild;

    public RgMasterProcessImpl(RgBuild rgBuild) {
        this.rgBuild = rgBuild;
    }

    @Override
    public NodeRunner createRunner() throws IOException, InterruptedException {
        RadarGunNodeAction masterAction = new RadarGunNodeAction(rgBuild.getBuild(), rgBuild.getNodes().getMaster().getName(),
                "RadarGun master ");
        rgBuild.getBuild().addAction(masterAction);
        String[] masterCmdLine = getMasterCmdLine();
        ProcStarter masterProcStarter = Functions.buildProcStarter(rgBuild, masterCmdLine, masterAction.getLogFile());
        return new NodeRunner(masterProcStarter, masterAction);
    }
    
    public String[] getMasterCmdLine() throws InterruptedException, IOException {
        MasterNode master = rgBuild.getNodes().getMaster();
        MasterScriptConfig masterScriptConfig = new MasterShellScript();
        masterScriptConfig.withNumberOfSlaves(rgBuild.getNodes().getSlaveCount())
                .withConfigPath(rgBuild.getRgBuilder().getScenarioSource().getTmpScenarioPath(rgBuild.getBuild())).withMasterHost(master.getHostname())
                .withScriptPath(rgBuild.getRgInstall().getExecutable(masterScriptConfig, rgBuild.getLauncher().getChannel()));

        String pluginPath = rgBuild.getRgBuilder().getPluginPath();
        String pluginConfigPath = rgBuild.getRgBuilder().getPluginConfigPath();
        String reporterPath = rgBuild.getRgBuilder().getReporterPath();
        if (pluginPath != null && !pluginPath.isEmpty()) {
            masterScriptConfig.withPlugin(pluginPath);
            if (pluginConfigPath != null && !pluginConfigPath.isEmpty()) {
                masterScriptConfig.withPluginConfig(pluginConfigPath);
            }
        }
        if (reporterPath != null && !reporterPath.isEmpty())
            masterScriptConfig.withReporter(reporterPath);

        String javaOpts = Resolver.buildVar(rgBuild.getBuild(), master.getAllJavaOpts());
        masterScriptConfig.withJavaOpts(javaOpts);
        
        String[] masterCmdLine = rgBuild.getRgBuilder().getScriptSource().getMasterCmdLine(rgBuild.getBuild().getWorkspace(), master, masterScriptConfig);
        return masterCmdLine;
    }
    
    @Override
    public int getProcessId() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void kill() {
        // TODO Auto-generated method stub

    }
    
}
