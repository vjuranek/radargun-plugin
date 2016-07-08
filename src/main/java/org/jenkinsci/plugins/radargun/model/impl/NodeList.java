package org.jenkinsci.plugins.radargun.model.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * List of nodes. The first one is master node, remaining are slave nodes.
 * 
 * @author vjuranek
 *
 */
public class NodeList {

    private final List<Node> nodes;

    public NodeList(Node master) {
        nodes = new ArrayList<Node>();
        nodes.add(master);
    }
    
    public NodeList(Node master, List<Node> slaves) {
        this(master);
        nodes.addAll(slaves);
    }
    
    public List<Node> getNodes() {
        return nodes;
    }

    public List<Node> asList() {
        return nodes;
    }
    
    public MasterNode getMaster() {
        return (MasterNode)nodes.get(0);
    }
    
    public List<Node> getSlaves() {
        return nodes.subList(1, nodes.size());
    }
    
    public void addSlave(Node slave) {
        nodes.add(slave);
    }
    
    public int getNodeCount() {
        return nodes.size();
    }
    
    public int getSlaveCount() {
        return nodes.size() - 1;
    }
}
