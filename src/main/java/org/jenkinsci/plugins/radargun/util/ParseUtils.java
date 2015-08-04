package org.jenkinsci.plugins.radargun.util;

import org.jenkinsci.plugins.radargun.config.NodeConfigParser;
import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.yaml.YamlNodeConfigParser;

public class ParseUtils {

    public static NodeList parseNodeList(String nodeList) {
        NodeConfigParser parser = new YamlNodeConfigParser();
        return parser.parseNodeList(nodeList);
    }
    
}
