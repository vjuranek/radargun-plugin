package org.jenkinsci.plugins.radargun.model;


/**
 * Base for RG master and slave exec scripts
 * 
 * @author vjuranek
 * 
 */
public interface NodeScriptConfig extends RgScriptConfig {

    public String getMasterHost();

    public String getOutputPath();

    public boolean isTailFollow();

    public boolean isWait();

    public String getPlugin();

    public String getPluginConfig();

    public NodeScriptConfig withMasterHost(String masterHostname);

    public NodeScriptConfig withOutput(String outputPath);

    public NodeScriptConfig withTailFollow();

    public NodeScriptConfig withWait();

    public NodeScriptConfig withPlugin(String plugin);

    public NodeScriptConfig withPluginConfig(String pluginConfig);

    public static enum Options {
        MASTER_HOST(new Option("-m", "getMasterHost", true)), 
        OUTPUT_PATH(new Option("-o", "getOutputPath", true)), 
        TAIL_FOLLOW(new Option("-t", "isTailFollow", false)), 
        WAIT(new Option("-w", "isWait", false)), 
        PLUGIN(new Option("--add-plugin", "getPlugin", true)), 
        PLUGIN_CONFIG(new Option("--add-config", "getPluginConfig", true));

        private Option option;

        private Options(Option option) {
            this.option = option;
        }

        public Option getOption() {
            return option;
        }
    }

}
