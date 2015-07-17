package org.jenkinsci.plugins.radargun.config;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jenkinsci.plugins.radargun.model.impl.Node;
import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.yaml.snakeyaml.Yaml;

public class YamlNodeConfigParser implements NodeConfigParser {

    public static final String NODES_KEY = "nodes";
    
    private final Yaml yaml;

    public YamlNodeConfigParser() {
        this.yaml = new Yaml();
    }

    @Override
    public NodeList parseNodeList(String nodesConfig) {
        @SuppressWarnings("unchecked")
        Map<String, Object> parsedConf = (Map<String, Object>) yaml.load(nodesConfig);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodesConf = (List<Map<String, Object>>) parsedConf.get(NODES_KEY);
        if (nodesConf.size() < 2)
            throw new IllegalArgumentException(
                    "Wrong node configuration, at least two nodes (one master and one slave) required!");

        Map<String, Object> masterConf = nodesConf.remove(0);
        String masterHost = masterConf.keySet().iterator().next();
        @SuppressWarnings("unchecked")
        Node master = Node.parseNode(masterHost, (Map<String, Object>)masterConf.get(masterHost));

        List<Node> nodes = new LinkedList<Node>();
        for (Map<String, Object> nodeConf : nodesConf) {
            String nodeHost = nodeConf.keySet().iterator().next();
            @SuppressWarnings("unchecked")
            Node node = Node.parseNode(nodeHost, (Map<String, Object>)nodeConf.get(nodeHost));
            nodes.add(node);
        }

        return new NodeList(master, nodes);
    }

}
