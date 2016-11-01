package org.jenkinsci.plugins.radargun.model;

/**
 * Represent RG master script, currently only shell script is provided by RG
 * 
 * @author vjuranek
 *
 */
public interface MasterScriptConfig extends NodeScriptConfig {

    public String getConfigPath();
    
    public int getSlaveNumber();
    
    public String getReporterPath();
    
    public MasterScriptConfig withConfigPath(String configPath);
    
    public MasterScriptConfig withNumberOfSlaves(int slaveNumber);
    
    public MasterScriptConfig withReporter(String reporterPath);
    
    public static enum Options {
        CONFIG_PATH(new Option("-c", "getConfigPath", true, false)), 
        SLAVE_NUMBER(new Option("-s", "getSlaveNumber", true, false)),
        REPORTER_PATH(new Option("--add-reporter", "getReporterPath", true, true));

        private Option option;

        private Options(Option option) {
            this.option = option;
        }

        public Option getOption() {
            return option;
        }
    }
}
