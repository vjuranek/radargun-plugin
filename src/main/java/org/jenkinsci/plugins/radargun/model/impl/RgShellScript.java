package org.jenkinsci.plugins.radargun.model.impl;

import org.apache.commons.lang.ArrayUtils;
import org.jenkinsci.plugins.radargun.model.RgScriptConfig;

/**
 * RG shell script base.
 * 
 * @author vjuranek
 *
 */
public abstract class RgShellScript implements RgScriptConfig {
    
    public static final String SHELL_EXEC = "/bin/sh";
    
    protected String scriptPath;
    
    @Override
    public abstract String getScriptName();
    
    @Override
    public String[] getInterpreter() {
        return new String[] {SHELL_EXEC};
    }
    
    @Override
    public String getScriptPath() {
        return scriptPath;
    }
    
    @Override
    public RgScriptConfig withScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
        return this;
    }
    
    @Override
    public String[] getScriptCmd() {
        return (String[])ArrayUtils.addAll(getInterpreter(), new String[] {getScriptName()});
    }
    
}
