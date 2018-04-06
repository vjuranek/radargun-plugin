package org.jenkinsci.plugins.radargun.config;

import java.util.List;

import org.jenkinsci.plugins.radargun.RadarGunBuilder;
import org.jenkinsci.plugins.radargun.RadarGunInstallation;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import jenkins.model.Jenkins;

public class RadarGunInstallationWrapper extends RadarGunInstance {

    private String radarGunName;
    
    @DataBoundConstructor
    public RadarGunInstallationWrapper(String radarGunName) {
        if (radarGunName == null) {
            throw new IllegalArgumentException("RG installation name cannot be null");
        }
        this.radarGunName = radarGunName;
    }
    
    public String getRadarGunName() {
        return radarGunName;
    }
    
    public String getName() {
        return getRadarGunName();
    }
    
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
