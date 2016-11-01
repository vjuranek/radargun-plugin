package org.jenkinsci.plugins.radargun.model.impl;

import static org.junit.Assert.assertArrayEquals;

import org.jenkinsci.plugins.radargun.model.MasterScriptConfig;
import org.junit.Test;

public class MasterShellScriptTest {
    
    @Test
    public void testCmdLine() {
        MasterScriptConfig master = new MasterShellScript();
        master.withNumberOfSlaves(10)
            .withPlugin("testPlugin")
            .withTailFollow()
            .withWait()
            .withJavaOpts("'-Daaa=bbb -Dccc=ddd'")
            .withScriptPath("/tmp/master.sh");
        
        assertArrayEquals(new String[] {"/bin/sh", "/tmp/master.sh", "-t", "-w", "--add-plugin", "testPlugin", "-J", "'-Daaa=bbb -Dccc=ddd'", "-s", "10"}, master.getScriptCmd());
    }
    
    @Test
    public void testMultiValues() {
        MasterScriptConfig master = new MasterShellScript();
        master.withNumberOfSlaves(10)
            .withReporter("reporter1 reporter2")
            .withPlugin("testPlugin1 testPlugin2")
            .withPluginConfig("config1 config2 config3")
            .withTailFollow()
            .withWait()
            .withJavaOpts("'-Daaa=bbb -Dccc=ddd'")
            .withScriptPath("/tmp/master.sh");
        
        assertArrayEquals(new String[] {"/bin/sh", "/tmp/master.sh", "-t", "-w", "--add-plugin", "testPlugin1", "--add-plugin", "testPlugin2", 
                "--add-config", "config1", "--add-config", "config2", "--add-config", "config3", "-J", "'-Daaa=bbb -Dccc=ddd'", "-s", "10", 
                "--add-reporter", "reporter1", "--add-reporter", "reporter2"}, master.getScriptCmd());
    }

}
