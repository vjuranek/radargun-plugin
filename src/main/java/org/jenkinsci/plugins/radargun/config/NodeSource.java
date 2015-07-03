package org.jenkinsci.plugins.radargun.config;

import hudson.DescriptorExtensionList;
import hudson.model.Describable;
import hudson.model.Descriptor;

import java.io.IOException;

import jenkins.model.Jenkins;

import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.util.Resolver;

public abstract class NodeSource implements Describable<NodeSource> {

    public abstract NodeList getNodesList(Resolver resolver) throws IOException, InterruptedException;

    @Override
    @SuppressWarnings("unchecked")
    public Descriptor<NodeSource> getDescriptor() {
        return Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    public static final DescriptorExtensionList<NodeSource, Descriptor<NodeSource>> all() {
        return Jenkins.getInstance().getDescriptorList(NodeSource.class);
    }

    public static abstract class NodeSourceDescriptor extends Descriptor<NodeSource> {
    }

}
