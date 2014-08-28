package org.jenkinsci.plugins.radargun;

import hudson.Launcher.ProcStarter;
import hudson.Proc;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class NodeRunner implements Runnable {
    
    private final ProcStarter procStarter;
    private final RadarGunNodeAction nodeAction;
    private CountDownLatch latch;
    
    public NodeRunner(ProcStarter procStarter, RadarGunNodeAction nodeAction) {
        this.procStarter = procStarter;
        this.nodeAction = nodeAction;
    }

    public void setLatch(final CountDownLatch latch) {
        this.latch = latch;
    }
    
    @Override
    public void run() {
        nodeAction.setInProgress(true);
        
        try {
            Proc proc = procStarter.start();
            int retCode = proc.join();
        } catch(IOException e) {
            //TODO log errors
        } catch(InterruptedException e) {
          //TODO log errors
        }
        
        nodeAction.setInProgress(false);
        latch.countDown();
    }

}
