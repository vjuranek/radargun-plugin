package org.jenkinsci.plugins.radargun;

import org.jenkinsci.plugins.radargun.model.impl.NodeList;

import hudson.Launcher;
import hudson.model.AbstractBuild;

/**
 * Convenient class for passing various build-related paramaters.
 * 
 * @author vjuranek
 *
 */
public class RgBuild {

    private final RadarGunBuilder rgBuilder;
    private final AbstractBuild<?, ?> build;
    private final Launcher launcher;
    private final NodeList nodes;
    private final RadarGunInstallation rgInstall;

    public RgBuild(RadarGunBuilder rgBuilder, AbstractBuild<?, ?> build, Launcher launcher, NodeList nodes, RadarGunInstallation rgInstall) {
        this.rgBuilder = rgBuilder;
        this.build = build;
        this.launcher = launcher;
        this.nodes = nodes;
        this.rgInstall = rgInstall;
    }
    
    public RadarGunBuilder getRgBuilder() {
        return rgBuilder;
    }

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public NodeList getNodes() {
        return nodes;
    }

    public RadarGunInstallation getRgInstall() {
        return rgInstall;
    }

}
