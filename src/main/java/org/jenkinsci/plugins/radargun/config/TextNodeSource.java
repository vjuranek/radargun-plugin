package org.jenkinsci.plugins.radargun.config;

import hudson.Extension;

import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.util.ParseUtils;
import org.jenkinsci.plugins.radargun.util.Resolver;
import org.kohsuke.stapler.DataBoundConstructor;

public class TextNodeSource extends NodeSource {

    private final String nodes;
    
    @DataBoundConstructor
    public TextNodeSource(String nodes) {
        this.nodes = nodes;
    }

    public String getNodes() {
        return nodes;
    }
    
    @Override
    public NodeList getNodesList(Resolver resolver) {
        return ParseUtils.parseNodeList(nodes);
    }
    
    @Extension
    public static class DescriptorImpl extends NodeSourceDescriptor {
        public String getDisplayName() {
            return "Text node list";
        }
    }
    
}
