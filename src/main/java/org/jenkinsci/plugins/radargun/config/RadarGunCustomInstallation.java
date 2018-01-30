package org.jenkinsci.plugins.radargun.config;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

public class RadarGunCustomInstallation extends RadarGunInstance {

    private static final String NAME = "__CUSTOM"; 
    
    private String displayName = "Custom";
    private String home;

    @DataBoundConstructor
    public RadarGunCustomInstallation(String home) {
        this.home = home;
    }

    public String getName() {
        return NAME;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public void setName(String name) {
        this.displayName = name;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    @Extension
    public static class DescriptorImpl extends RadarGunInstallationDescriptor {
        public String getDisplayName() {
            return "RadarGun custom installation";
        }
    }
}
