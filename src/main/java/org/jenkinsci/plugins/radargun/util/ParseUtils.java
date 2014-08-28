package org.jenkinsci.plugins.radargun.util;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.radargun.model.Node;
import org.jenkinsci.plugins.radargun.model.NodeList;

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
        String[] lines = nodeList.split(System.getProperty("line.separator"));
        Node master = Node.parseNode(lines[0]);
        List<Node> slaves = new ArrayList<Node>();
        for(int i = 1; i < lines.length; i++) {
            slaves.add(Node.parseNode(lines[i]));
        }
        return new NodeList(master, slaves);
    }
    
}
