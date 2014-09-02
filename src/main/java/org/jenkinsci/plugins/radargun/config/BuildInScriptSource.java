package org.jenkinsci.plugins.radargun.config;

import hudson.Extension;

import java.io.File;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * BuildInScriptSource
 * 
 * @author vjuranek
 *
 */
public class BuildInScriptSource extends ScriptSource {

    private static final String SCRIPT_DIR = "/scripts/";
    private static final String MASTER_SCRIPT_NAME = "start_master.sh";
    private static final String SLAVE_SCRIPT_NAME = "start_slave.sh";
    
    private static final String MASTER_SCRIPT_PATH = BuildInScriptSource.class.getResource(SCRIPT_DIR + MASTER_SCRIPT_NAME).getPath();
    private static final String SLAVE_SCRIPT_PATH = BuildInScriptSource.class.getResource(SCRIPT_DIR + SLAVE_SCRIPT_NAME).getPath();
    
    static {
        new File(MASTER_SCRIPT_PATH).setExecutable(true);
        new File(SLAVE_SCRIPT_PATH).setExecutable(true);
    }
    
    @DataBoundConstructor
    public BuildInScriptSource() {
        //NO-OP
        
    }
    
    public String getMasterScriptPath() {
        return MASTER_SCRIPT_PATH;
    }
    
    public String getSlaveScriptPath() {
        return SLAVE_SCRIPT_PATH;
    }

    @Extension
    public static class DescriptorImpl extends ScriptSourceDescriptor {
        public String getDisplayName() {
            return "Build-in script source";
        }
    }


}
