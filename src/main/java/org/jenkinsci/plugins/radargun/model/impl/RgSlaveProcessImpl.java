package org.jenkinsci.plugins.radargun.model.impl;

import java.io.FileOutputStream;
import java.io.IOException;

import org.jenkinsci.plugins.radargun.NodeRunner;
import org.jenkinsci.plugins.radargun.RadarGunNodeAction;
import org.jenkinsci.plugins.radargun.RgBuild;
import org.jenkinsci.plugins.radargun.model.RgSlaveProcess;
import org.jenkinsci.plugins.radargun.model.SlaveScriptConfig;
import org.jenkinsci.plugins.radargun.util.Functions;
import org.jenkinsci.plugins.radargun.util.Resolver;

import hudson.Launcher.ProcStarter;

public class RgSlaveProcessImpl extends AbstractRgProcess implements RgSlaveProcess {
    
    private final RgBuild rgBuild;
    private final int slaveId;
    
    public RgSlaveProcessImpl(RgBuild rgBuild, int slaveId) {
        this.rgBuild = rgBuild;
        this.slaveId = slaveId;
    }

    @Override
    public NodeRunner createRunner() throws IOException, InterruptedException {
        Node slave = rgBuild.getNodes().getSlaves().get(slaveId);
        RadarGunNodeAction slaveAction = new RadarGunNodeAction(rgBuild.getBuild(), slave.getName());
        rgBuild.getBuild().addAction(slaveAction);

        String[] slaveCmdLine = getSlaveCmdLine();
        ProcStarter slaveProcStarter = Functions.buildProcStarter(rgBuild, slaveCmdLine, new FileOutputStream(slaveAction.getLogFile()));
        return new NodeRunner(slaveProcStarter, slaveAction);
    }

    public String[] getSlaveCmdLine() throws InterruptedException, IOException {
        SlaveScriptConfig slaveScriptConfig = new SlaveShellScript();
        slaveScriptConfig.withSlaveIndex(slaveId).withMasterHost(rgBuild.getNodes().getMaster().getHostname())
                .withScriptPath(rgBuild.getRgInstall().getExecutable(slaveScriptConfig, rgBuild.getLauncher().getChannel()));
        
        String pluginPath = rgBuild.getRgBuilder().getPluginPath();
        String pluginConfigPath = rgBuild.getRgBuilder().getPluginConfigPath();
        if (pluginPath != null && !pluginPath.isEmpty()) {
            slaveScriptConfig.withPlugin(pluginPath);
            if (pluginConfigPath != null && !pluginConfigPath.isEmpty()) {
                slaveScriptConfig.withPluginConfig(pluginConfigPath);
            }
        }

        Node slave = rgBuild.getNodes().getSlaves().get(slaveId);
        String javaOpts = Resolver.buildVar(rgBuild.getBuild(), slave.getAllJavaOpts());
        slaveScriptConfig.withJavaOpts(javaOpts);
        
        String[] slaveCmdLine = rgBuild.getRgBuilder().getScriptSource().getSlaveCmdLine(slaveId, rgBuild, slaveScriptConfig);
        return slaveCmdLine;
    }

}
