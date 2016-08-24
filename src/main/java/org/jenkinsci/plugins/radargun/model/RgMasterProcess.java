package org.jenkinsci.plugins.radargun.model;

import java.io.IOException;

public interface RgMasterProcess extends RgProcess {

    public Integer getProcessId();
    public boolean kill() throws IOException, InterruptedException;
}
