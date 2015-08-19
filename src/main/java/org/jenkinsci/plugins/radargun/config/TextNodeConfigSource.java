package org.jenkinsci.plugins.radargun.config;

import hudson.Extension;

import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.util.ParseUtils;
import org.jenkinsci.plugins.radargun.util.Resolver;
import org.kohsuke.stapler.DataBoundConstructor;

public class TextNodeConfigSource extends NodeConfigSource {

    private final String nodes;
    
    @DataBoundConstructor
    public TextNodeConfigSource(String nodes) {
        this.nodes = nodes;
    }

    public String getNodes() {
        return nodes;
    }
    
    @Override
    public NodeList getNodesList() {
        return ParseUtils.parseNodeList(Resolver.doResolve(nodes));
    }
    
    @Extension
    public static class DescriptorImpl extends NodeSourceDescriptor {
        public String getDisplayName() {
            return "Text node list";
        }
    }
    
}
