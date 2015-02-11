package org.jenkinsci.plugins.radargun.model;

public interface MasterScriptConfig extends NodeScriptConfig {

    public String getConfigPath();
    
    public int getSlaveNumber();
    
    public String getReporterPath();
    
    public MasterScriptConfig withConfigPath(String configPath);
    
    public MasterScriptConfig withNumberOfSlaves(int slaveNumber);
    
    public MasterScriptConfig withReporter(String reporterPath);
    
    public static enum Options {
        CONFIG_PATH(new Option("-c", "getConfigPath", true)), 
        SLAVE_NUMBER(new Option("-s", "getSlaveNumber", true)),
        REPORTER_PATH(new Option("--add-reporter", "getReporterPath", true));

        private Option option;

        private Options(Option option) {
            this.option = option;
        }

        public Option getOption() {
            return option;
        }
    }
}
