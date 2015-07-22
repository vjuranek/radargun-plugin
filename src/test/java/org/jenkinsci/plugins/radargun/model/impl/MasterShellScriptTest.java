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

}
