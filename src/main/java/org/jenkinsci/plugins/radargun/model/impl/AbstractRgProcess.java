package org.jenkinsci.plugins.radargun.model.impl;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.radargun.NodeRunner;
import org.jenkinsci.plugins.radargun.model.RgProcess;

public abstract class AbstractRgProcess implements RgProcess {

    private static Logger LOGGER = Logger.getLogger(AbstractRgProcess.class.getName());

    protected CompletableFuture<Integer> processFuture;

    @Override
    public abstract NodeRunner createRunner() throws IOException, InterruptedException;

    @Override
    public void start(ExecutorService executorService) throws IllegalStateException {
        try {
        processFuture = CompletableFuture.supplyAsync(createRunner()).exceptionally((e) -> {
            LOGGER.log(Level.WARNING, "Execution of RG process has failed", e.fillInStackTrace());
            return new Integer(1);
        });
        } catch(IOException|InterruptedException e) {
            LOGGER.log(Level.WARNING, "Some of the previous steps has failed or was interrupted", e.fillInStackTrace());
        }
    }

    @Override
    public CompletableFuture<Integer> getProcessFuture() {
        return processFuture;
    }

    @Override
    public int waitForResult() {
        return processFuture.join().intValue();
    }

    @Override
    public void cancel() {
        processFuture.cancel(true);
    }

}
