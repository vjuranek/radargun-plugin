package org.jenkinsci.plugins.radargun.config;

import java.io.IOException;

import hudson.DescriptorExtensionList;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

import org.jenkinsci.plugins.radargun.model.impl.NodeList;

public abstract class NodeSource implements Describable<NodeSource> {

    public abstract NodeList getNodesList() throws IOException;

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
