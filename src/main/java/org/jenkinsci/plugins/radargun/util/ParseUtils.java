package org.jenkinsci.plugins.radargun.util;

import org.jenkinsci.plugins.radargun.config.NodeConfigParser;
import org.jenkinsci.plugins.radargun.config.YamlNodeConfigParser;
import org.jenkinsci.plugins.radargun.model.impl.NodeList;

public class ParseUtils {

    /**
     * 
     * Parse node list. Expected format is
     * <ul>
     *   <li> Each line is one machine </li>
     *   <li> The first line is master, others are slaves </li>
     *   <li> The first sequence of the line is machine name or its IP address, eventually can continue with space and JVM options for process started on this machine </li>
     *   <li> Additional JVM option are added to default JVM option, not overwrite them </li>
     * </ul>
     * 
     */
    public static NodeList parseNodeList(String nodeList) {
        NodeConfigParser parser = new YamlNodeConfigParser();
        return parser.parseNodeList(nodeList);
    }
    
}
