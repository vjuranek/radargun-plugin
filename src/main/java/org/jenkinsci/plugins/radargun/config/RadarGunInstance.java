package org.jenkinsci.plugins.radargun.config;

import hudson.DescriptorExtensionList;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

public abstract class RadarGunInstance implements Describable<RadarGunInstance> {
    
    public abstract String getName();
    
    @Override
    @SuppressWarnings("unchecked")
    public Descriptor<RadarGunInstance> getDescriptor() {
        return Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    public static final DescriptorExtensionList<RadarGunInstance, Descriptor<RadarGunInstance>> all() {
        return Jenkins.getInstance().getDescriptorList(RadarGunInstance.class);
    }

    public static abstract class RadarGunInstallationDescriptor extends Descriptor<RadarGunInstance> {
    }

}
