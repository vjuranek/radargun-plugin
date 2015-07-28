package org.jenkinsci.plugins.radargun.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hudson.FilePath;

import java.io.File;

import org.junit.Test;

/**
 * 
 * @author vjuranek
 *
 */
public class FunctionsTest {
    
    @Test
    public void testConvertWsToCanonicalPath() throws Exception {
        File f = new File("test.txt");
        String canonPath = Functions.convertWsToCanonicalPath(new FilePath(f));
        assertEquals(f.getCanonicalPath(), canonPath);
    }
    
    @Test
    public void testCmdArrayToString() {
        String[] cmd = {"aaa", "bbb", "ccc"};
        assertEquals("aaa bbb ccc", Functions.cmdArrayToString(cmd));
        cmd = new String[] {"aaa"};
        assertEquals("aaa", Functions.cmdArrayToString(cmd));
        cmd = new String[] {"aaa", "'bbb'"};
        assertEquals("aaa 'bbb'", Functions.cmdArrayToString(cmd));
        cmd = new String[] {"aaa; bbb", "ccc"};
        assertEquals("aaa; bbb ccc", Functions.cmdArrayToString(cmd));
    }
    
    @Test
    public void testMakeExecutable() throws Exception {
        File f = File.createTempFile("test", ".sh");
        Functions.makeExecutable(f.getAbsolutePath());
        assertTrue(f.canExecute());
        
        Process p = new ProcessBuilder(f.getAbsolutePath()).start();
        assertEquals(0, p.waitFor());
    }
    

}
