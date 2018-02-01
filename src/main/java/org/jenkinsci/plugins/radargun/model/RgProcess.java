package org.jenkinsci.plugins.radargun.model;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.jenkinsci.plugins.radargun.NodeRunner;

/**
 * Crestes and represent remote RG process.
 * 
 * @author vjuranek
 *
 */
public interface RgProcess {
    
    public NodeRunner createRunner() throws IOException, InterruptedException ;
    public void start(ExecutorService executorService) throws IllegalStateException;
    public CompletableFuture<Integer> getProcessFuture();
    public int waitForResult() throws ExecutionException, InterruptedException;
    public void cancel();

}
