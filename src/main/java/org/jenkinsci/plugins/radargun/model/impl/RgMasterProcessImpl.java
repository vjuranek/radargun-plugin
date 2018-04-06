package org.jenkinsci.plugins.radargun.model.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.radargun.NodeRunner;
import org.jenkinsci.plugins.radargun.RadarGunNodeAction;
import org.jenkinsci.plugins.radargun.RgBuild;
import org.jenkinsci.plugins.radargun.model.MasterScriptConfig;
import org.jenkinsci.plugins.radargun.model.RgMasterProcess;
import org.jenkinsci.plugins.radargun.util.Functions;
import org.jenkinsci.plugins.radargun.util.Resolver;

import hudson.Launcher.ProcStarter;

public class RgMasterProcessImpl extends AbstractRgProcess implements RgMasterProcess {

    private static Logger LOGGER = Logger.getLogger(RgMasterProcessImpl.class.getName());

    private static final int MAX_PID_FILE_RETRIES = 5; // how many times should we try if RG master.pid file ws created
    private static final int RETRY_DELAY = 5000; // how long (in milliseconds) should we wait before we try next time

    private final RgBuild rgBuild;
    private Integer rgMasterPid = null;
    private RadarGunNodeAction masterAction;

    public RgMasterProcessImpl(RgBuild rgBuild) {
        this.rgBuild = rgBuild;
    }

    @Override
    public NodeRunner createRunner() throws IOException, InterruptedException {
        masterAction = new RadarGunNodeAction(rgBuild.getBuild(), rgBuild.getNodes().getMaster().getName(),
                "RadarGun master ");
        rgBuild.getBuild().addAction(masterAction);
        String[] masterCmdLine = getMasterCmdLine();
        ProcStarter masterProcStarter = Functions.buildProcStarter(rgBuild, masterCmdLine, new FileOutputStream(masterAction.getLogFile()));
        return new NodeRunner(masterProcStarter, masterAction);
    }

    public String[] getMasterCmdLine() throws InterruptedException, IOException {
        MasterNode master = rgBuild.getNodes().getMaster();
        MasterScriptConfig masterScriptConfig = new MasterShellScript();
        masterScriptConfig.withNumberOfSlaves(rgBuild.getNodes().getSlaveCount())
                .withConfigPath(rgBuild.getRgBuilder().getScenarioSource().getTmpScenarioPath(rgBuild.getBuild()))
                .withMasterHost(master.getHostname()).withScriptPath(
                        rgBuild.getRgInstall().getExecutable(masterScriptConfig, rgBuild.getLauncher().getChannel()));

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

        String[] masterCmdLine = rgBuild.getRgBuilder().getScriptSource().getMasterCmdLine(rgBuild, masterScriptConfig);
        return masterCmdLine;
    }

    @Override
    public Integer getProcessId() {
        if (rgMasterPid == null) {
            try {
                rgMasterPid = readRemotePid();
            } catch (IOException | InterruptedException e) {
                LOGGER.log(Level.WARNING, "Cannot read remote RG master PID", e);
            }
        }
        return rgMasterPid;
    }

    @Override
    public boolean kill() throws IOException, InterruptedException {
        Integer pid = getProcessId();
        if (pid == null) {
            LOGGER.log(Level.WARNING, "Remote RG master PID in null, skipping RG master kill");
            return false;
        }
        
        String[] cmd = Functions.buildRemoteCmd(rgBuild, rgBuild.getNodes().getMaster().getHostname(),
                new String[] { "/usr/bin/kill", "-9", pid.toString() });
        ProcStarter killPidProc = Functions.buildProcStarter(rgBuild, cmd, new FileOutputStream(masterAction.getLogFile(), true));
        killPidProc.start();
        return killPidProc.join() == 0;
    }

    private Integer readRemotePid() throws IOException, InterruptedException {
        String workspace = Functions.getRemoteWorkspace(rgBuild).getRemote();

        // check if RG master.pid file exists and eventually wait for it to be created
        String[] cmd = Functions.buildRemoteCmd(rgBuild, rgBuild.getNodes().getMaster().getHostname(),
                new String[] { "/usr/bin/test", "-f", workspace + "/master.pid" });
        ProcStarter masterPidFileProc = Functions.buildProcStarter(rgBuild, cmd, new FileOutputStream(masterAction.getLogFile(), true));
        int retCode = 1;
        int retries = 0;
        do {
            if (retries != 0) {
                Thread.sleep(RETRY_DELAY);
            }
            masterPidFileProc.start();
            retCode = masterPidFileProc.join();
            retries++;
        } while (retCode != 0 && retries <= MAX_PID_FILE_RETRIES);
        if (retCode != 0) {
            throw new IllegalStateException("RG master.pid was not created during wating period, exiting!");
        }

        // master.pid exists, read the file content
        cmd = Functions.buildRemoteCmd(rgBuild, rgBuild.getNodes().getMaster().getHostname(),
                new String[] { "/usr/bin/cat", workspace + "/master.pid" });
        ProcStarter masterPidProc = Functions.buildProcStarter(rgBuild, cmd, new FileOutputStream(masterAction.getLogFile(), true));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        masterPidProc.stdout(baos);
        retCode = masterPidProc.join();
        if (retCode != 0) {
            throw new IOException("Unable to read master.pid file!");
        }
        String pid = new String(baos.toByteArray(), Charset.defaultCharset());
        return Integer.valueOf(pid.substring(0, pid.length() - 1));
    }

}
