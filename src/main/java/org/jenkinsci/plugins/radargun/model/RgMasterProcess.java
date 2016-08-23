package org.jenkinsci.plugins.radargun.model;

public interface RgMasterProcess extends RgProcess {

    public int getProcessId();
    public void kill();
}
