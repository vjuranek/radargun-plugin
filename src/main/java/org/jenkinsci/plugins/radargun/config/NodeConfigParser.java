package org.jenkinsci.plugins.radargun.config;

import org.jenkinsci.plugins.radargun.model.impl.NodeList;

public interface NodeConfigParser {

    public NodeList parseNodeList(String nodeList);
    
}
