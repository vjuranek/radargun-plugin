package org.jenkinsci.plugins.radargun.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.jenkinsci.plugins.radargun.model.MasterScriptConfig;
import org.jenkinsci.plugins.radargun.model.SlaveScriptConfig;

public class SlaveShellScript extends NodeShellScript implements SlaveScriptConfig {

    public static final String SLAVE_SCRIPT_NAME = "slave.sh";
    
    protected int slaveIndex;
    protected String slaveName;
    
    @Override
    public String getScriptName() {
        return SLAVE_SCRIPT_NAME;
    }
    
    @Override
    public int getSlaveIndex() {
        return slaveIndex;
    }

    @Override
    public String getSlaveName() {
        return slaveName;
    }

    @Override
    public SlaveScriptConfig withSlaveIndex(int slaveIndex) {
        this.slaveIndex = slaveIndex;
        return this;
    }

    @Override
    public SlaveScriptConfig withSlaveName(String slaveName) {
        this.slaveName = slaveName;
        return this;
    }

    @Override
    public String[] getScriptCmd() {
        return (String[])ArrayUtils.addAll(super.getScriptCmd(), optionToArray());
    }
    
    private String[] optionToArray() {
        List<String> opts = new ArrayList<String>();
        for(SlaveScriptConfig.Options o : SlaveScriptConfig.Options.values()) {
            opts.addAll(o.getOption().getCmdOption(this));
        }
        return opts.toArray(new String[opts.size()]);
    }

}
