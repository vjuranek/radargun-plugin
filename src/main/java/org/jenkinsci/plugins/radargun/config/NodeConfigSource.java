package org.jenkinsci.plugins.radargun.config;

import hudson.DescriptorExtensionList;
import hudson.model.Describable;
import hudson.model.Descriptor;

import java.io.IOException;

import jenkins.model.Jenkins;

import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.util.Resolver;

/**
 * Configuration provider for nodes. Contains information on which nodes RG should run and configration of the node,
 * typically environment variables definitions of java properties.
 * 
 * @author vjuranek
 * 
 */
public abstract class NodeConfigSource implements Describable<NodeConfigSource> {

    public abstract NodeList getNodesList(Resolver resolver) throws IOException, InterruptedException;

    @Override
    @SuppressWarnings("unchecked")
    public Descriptor<NodeConfigSource> getDescriptor() {
        return Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    public static final DescriptorExtensionList<NodeConfigSource, Descriptor<NodeConfigSource>> all() {
        return Jenkins.getInstance().getDescriptorList(NodeConfigSource.class);
    }

    public static abstract class NodeSourceDescriptor extends Descriptor<NodeConfigSource> {
    }

}
