package org.jenkinsci.plugins.radargun.model.impl;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.jenkinsci.plugins.radargun.NodeRunner;
import org.jenkinsci.plugins.radargun.model.RgProcess;

public abstract class AbstractRgProcess implements RgProcess {
    
    protected Future<Integer> processFuture;

    @Override
    public abstract NodeRunner createRunner() throws IOException, InterruptedException;
    
    @Override
    public void start(ExecutorService executorService) throws IllegalStateException {
        try {
            processFuture = executorService.submit(createRunner());
        } catch (IOException|InterruptedException e) {
            throw new IllegalStateException("Some of the previous steps has failed or was interrupted", e.fillInStackTrace());
        }
    }

    @Override
    public Future<Integer> getProcessFuture() {
        return processFuture;
    }

    @Override
    public int waitForResult() throws ExecutionException, InterruptedException {
        return processFuture.get().intValue();
    }

    @Override
    public void cancel() {
        processFuture.cancel(true);
    }

}
