package org.jenkinsci.plugins.radargun;

import hudson.Launcher.ProcStarter;
import hudson.Proc;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class NodeRunner implements Runnable {
    
    private ProcStarter procStarter;
    private CountDownLatch latch;
    
    public NodeRunner(ProcStarter procStarter, CountDownLatch latch) {
        this.procStarter = procStarter;
        this.latch = latch;
    }

    @Override
    public void run() {
        System.out.println("Starting " + procStarter.toString());
        try {
            Proc proc = procStarter.start();
            int retCode = proc.join();
        } catch(IOException|InterruptedException e) {
            //TODO log errors
        }
        
        latch.countDown();
    }

}
