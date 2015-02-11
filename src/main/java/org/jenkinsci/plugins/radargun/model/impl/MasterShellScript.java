package org.jenkinsci.plugins.radargun.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.jenkinsci.plugins.radargun.model.MasterScriptConfig;


public class MasterShellScript extends NodeShellScript implements MasterScriptConfig {

    public static final String MASTER_SCRIPT_NAME = "master.sh";
    
    protected String configPath;
    protected int slaveNumber;
    protected String reporterPath;
    
    @Override
    public String getScriptName() {
        return MASTER_SCRIPT_NAME;
    }
    
    @Override
    public String getConfigPath() {
        return configPath;
    }

    @Override
    public int getSlaveNumber() {
        return slaveNumber;
    }

    @Override
    public String getReporterPath() {
        return reporterPath;
    }

    @Override
    public MasterScriptConfig withConfigPath(String configPath) {
        this.configPath = configPath;
        return this;
    }

    @Override
    public MasterScriptConfig withNumberOfSlaves(int slaveNumber) {
        this.slaveNumber = slaveNumber;
        return this;
    }

    @Override
    public MasterScriptConfig withReporter(String reporterPath) {
        this.reporterPath = reporterPath;
        return this;
    }

    @Override
    public String[] getScriptCmd() {
        return (String[])ArrayUtils.addAll(super.getScriptCmd(), optionToArray());
    }
    
    private String[] optionToArray() {
        List<String> opts = new ArrayList<String>();
        for(MasterScriptConfig.Options o : MasterScriptConfig.Options.values()) {
            opts.addAll(o.getOption().getCmdOption(this));
        }
        return opts.toArray(new String[opts.size()]);
    }

}
