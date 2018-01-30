package org.jenkinsci.plugins.radargun.config;

import java.util.List;

import org.jenkinsci.plugins.radargun.RadarGunBuilder;
import org.jenkinsci.plugins.radargun.RadarGunInstallation;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import jenkins.model.Jenkins;

public class RadarGunInstallationWrapper extends RadarGunInstance {

    private String radarGunName;
    //private transient RadarGunInstallation install;
    
    @DataBoundConstructor
    public RadarGunInstallationWrapper(String radarGunName) {
        if (radarGunName == null) {
            throw new IllegalArgumentException("RG installation name cannot be null");
        }
        this.radarGunName = radarGunName;
        //this.install = Jenkins.getInstance().getDescriptorByType(RadarGunBuilder.DescriptorImpl.class).getInstallation(radarGunName);
    }
    
    /*public RadarGunInstallationWrapper(RadarGunInstallation install) {
        if (install == null) {
            throw new IllegalArgumentException("RG installation cannot be null");
        }
        this.install = install;
    }
    
    public RadarGunInstallation getInsall() {
        return install;
    }*/
    
    public String getRadarGunName() {
        return radarGunName;
    }
    
    public String getName() {
        return getRadarGunName();
    }
    
    /*public String getHome() {
        return install.getHome();
    }
    
    public String getDisplayName() {
        return install.getDisplayName();
    }*/
    
    @Extension
    public static class DescriptorImpl extends RadarGunInstallationDescriptor {
        public String getDisplayName() {
            return "Pre-defined RadarGun installation";
        }
        
        public List<RadarGunInstallation> getInstallations() {
            return Jenkins.getInstance().getDescriptorByType(RadarGunBuilder.DescriptorImpl.class).getInstallations();
        }
    }
}
