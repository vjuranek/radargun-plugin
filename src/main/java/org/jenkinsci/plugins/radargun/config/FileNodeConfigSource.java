package org.jenkinsci.plugins.radargun.config;

import hudson.Extension;
import hudson.FilePath;

import java.io.File;
import java.io.IOException;

import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.util.ParseUtils;
import org.jenkinsci.plugins.radargun.util.Resolver;
import org.kohsuke.stapler.DataBoundConstructor;

public class FileNodeConfigSource extends NodeConfigSource {

    private final String nodeListPath;
    
    @DataBoundConstructor
    public FileNodeConfigSource(String nodeListPath) {
        this.nodeListPath = nodeListPath;
    }

    public String getNodeListPath() {
        return nodeListPath;
    }
    
    @Override
    public NodeList getNodesList(Resolver resolver) throws IOException, InterruptedException {
        String nodeListPathRes = resolver.doResolve(nodeListPath);
        FilePath fp = new FilePath(new File(nodeListPathRes));
        String nodes = resolver.doResolve(fp.readToString());
        return ParseUtils.parseNodeList(nodes);
    }
    
    @Extension
    public static class DescriptorImpl extends NodeSourceDescriptor {
        public String getDisplayName() {
            return "Node list from file";
        }
    }
    
}
