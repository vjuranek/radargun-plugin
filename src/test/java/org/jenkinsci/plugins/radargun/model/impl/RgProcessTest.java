package org.jenkinsci.plugins.radargun.model.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jenkinsci.plugins.radargun.NodeRunner;
import org.junit.Test;

public class RgProcessTest {
    
    private static final int PROCESSES = Runtime.getRuntime().availableProcessors() + 10;
    private static final ExecutorService execServis = Executors.newFixedThreadPool(PROCESSES);
    
    @Test //https://github.com/vjuranek/radargun-plugin/issues/74
    public void testRgProcessStarter() {
        final CyclicBarrier barrier = new CyclicBarrier(PROCESSES);
        TestRgProcess[] processes = new TestRgProcess[PROCESSES];
        for (int i = 0; i < PROCESSES; i++) {
            TestRgProcess proc = new TestRgProcess(barrier);
            processes[i] = proc;
            proc.start(execServis);
        }
        
        int sum = 0;
        for (int i = 0; i < PROCESSES; i++) {
            try {
                sum += processes[i].getProcessFuture().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        
        assertEquals(PROCESSES, sum);
    }
    
    public static class TestRgProcess extends AbstractRgProcess {
        
        private final CyclicBarrier barrier;
        
        public TestRgProcess(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public NodeRunner createRunner() throws IOException, InterruptedException {
            return new TestNodeRunner(barrier);
        }
        
    }
    
    public static class TestNodeRunner extends NodeRunner {

        private final CyclicBarrier barrier;
        
        public TestNodeRunner(CyclicBarrier barrier) {
            super(null, null);
            this.barrier = barrier;
        }

        @Override
        public Integer get() {
            boolean finished = false;
            try {
                barrier.await(1, TimeUnit.SECONDS);
                finished = true;
            } catch (TimeoutException e) {
                //no-op
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
            }
            return finished ? Integer.valueOf(1) : Integer.valueOf(0);
        }

    }

}
