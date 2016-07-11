package org.jenkinsci.plugins.radargun.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import hudson.FilePath;

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
    
    @Test
    public void testUserCmdsToArrayBefore() {
       List<String> cmdsList = new LinkedList<String>();
       cmdsList.add("echo \"aaa\" > /tmp/aaa.txt");
       cmdsList.add("ls -la /tmp");
       String[] cmds = Functions.userCmdsToArray(cmdsList, ';', false);
       assertEquals(9, cmds.length);
       assertEquals("echo", cmds[0]);
       assertEquals("\"aaa\"", cmds[1]);
       assertEquals(">", cmds[2]);
       assertEquals("/tmp/aaa.txt", cmds[3]);
       assertEquals(";", cmds[4]);
       assertEquals("ls", cmds[5]);
       assertEquals("-la", cmds[6]);
       assertEquals("/tmp", cmds[7]);
       assertEquals(";", cmds[8]);
    }
    
    @Test
    public void testUserCmdsToArrayAfter() {
       List<String> cmdsList = new LinkedList<String>();
       cmdsList.add("echo \"aaa\" > /tmp/aaa.txt");
       cmdsList.add("ls -la /tmp");
       String[] cmds = Functions.userCmdsToArray(cmdsList, ';', true);
       assertEquals(9, cmds.length);
       assertEquals(";", cmds[0]);
       assertEquals("echo", cmds[1]);
       assertEquals("\"aaa\"", cmds[2]);
       assertEquals(">", cmds[3]);
       assertEquals("/tmp/aaa.txt", cmds[4]);
       assertEquals(";", cmds[5]);
       assertEquals("ls", cmds[6]);
       assertEquals("-la", cmds[7]);
       assertEquals("/tmp", cmds[8]);
    }

}
