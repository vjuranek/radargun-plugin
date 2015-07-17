package org.jenkinsci.plugins.radargun.model.impl;

import static org.junit.Assert.assertArrayEquals;

import org.jenkinsci.plugins.radargun.model.SlaveScriptConfig;
import org.junit.Test;

public class SlaveShellScriptTest {

    @Test
    public void testCmdLine() {
        SlaveScriptConfig slave = new SlaveShellScript();
        slave.withSlaveIndex(1)
            .withPlugin("testPlugin")
            .withTailFollow() 
            .withWait() 
            .withScriptPath("/tmp/slave.sh");
         
        assertArrayEquals(new String[] {"/bin/sh", "/tmp/slave.sh", "-t", "-w", "--add-plugin", "testPlugin", "-i","1"}, slave.getScriptCmd());
    }
}
