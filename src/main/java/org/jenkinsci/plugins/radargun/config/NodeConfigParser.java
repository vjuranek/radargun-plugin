package org.jenkinsci.plugins.radargun.config;

import org.jenkinsci.plugins.radargun.model.impl.NodeList;

/**
 * Parser for node configuration. Provided configuration, parse it and return {@link NodeList} with filled data.
 * 
 * @author vjuranek
 *
 */
public interface NodeConfigParser {

    public NodeList parseNodeList(String nodeList);
    
}
