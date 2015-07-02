package org.jenkinsci.plugins.radargun.model.impl;

import java.io.File;

import org.jenkinsci.plugins.radargun.model.RgScriptConfig;

/**
 * RG shell script base.
 * 
 * @author vjuranek
 *
 */
public abstract class RgShellScript implements RgScriptConfig {
    
    public static final char SEP = File.separatorChar;
    public static final String SHELL_EXEC = String.format("%cbin%csh", SEP, SEP);
    
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
        //return (String[])ArrayUtils.add(getInterpreter(), getScriptPath());
        return new String[] {SHELL_EXEC, getScriptPath() + SEP + getScriptName()};
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(String s : getScriptCmd()) {
            sb.append(s);
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
}
