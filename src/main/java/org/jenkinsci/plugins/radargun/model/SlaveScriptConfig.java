package org.jenkinsci.plugins.radargun.model;

/**
 * Represent RG slave script, currently only shell script is provided by RG
 * 
 * @author vjuranek
 *
 */
public interface SlaveScriptConfig extends NodeScriptConfig {
    
    public int getSlaveIndex();
    
    public String getSlaveName();

    public SlaveScriptConfig withSlaveIndex(int slaveIndex);
    
    public SlaveScriptConfig withSlaveName(String slaveName);
    
    public static enum Options {
        SLAVE_INDEX(new Option("-i", "getSlaveIndex", true, false)), 
        SLAVE_NAME(new Option("-n", "getSlaveName", true, false));

        private Option option;

        private Options(Option option) {
            this.option = option;
        }

        public Option getOption() {
            return option;
        }
    }
}
