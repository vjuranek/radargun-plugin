package org.jenkinsci.plugins.radargun.config;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jenkinsci.plugins.radargun.model.impl.MasterNode;
import org.jenkinsci.plugins.radargun.model.impl.Node;
import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.yaml.snakeyaml.Yaml;

/**
 * {@link NodeConfigParser} for YAML configurations. YAML file can contain arbitrary section, the only required is
 * {@code nodes} list, containing list of nodes, each represented by it's hostname followed by a map of options. This
 * oprions can contain following elements:
 * <ul>
 * <li>{@code jvmOtions} and {@code envVars}. {@code jvmOtions} is a plain string containing JVM options like -Xmx etc.</li>
 * <li>{@code javaProps} is a map of java propertied to be passed to RG startup script. Typically should be used for
 * setting up variables used in RG scenarios. Properties are entered without "-D" prefix, this will be added later on
 * automatically.</li>
 * <li>{@code envVars} is a map of environment variables and their values, which should be exported to given host.</li>
 * </ul>
 * 
 * @author vjuranek
 * 
 */
public class YamlNodeConfigParser implements NodeConfigParser {

    public static final String NODES_KEY = "nodes";
    public static final String MASTER_FQDN = "fqdn";
    public static final String JVM_OPTS_KEY = "jvmOpts";
    public static final String JAVA_PROPS_KEY = "javaProps";
    public static final String ENV_VARS_KEY = "envVars";

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
        MasterNode master = parseMasterNode(masterHost, (Map<String, Object>) masterConf.get(masterHost));

        List<Node> nodes = new LinkedList<Node>();
        for (Map<String, Object> nodeConf : nodesConf) {
            String nodeHost = nodeConf.keySet().iterator().next();
            @SuppressWarnings("unchecked")
            Node node = parseNode(nodeHost, (Map<String, Object>) nodeConf.get(nodeHost));
            nodes.add(node);
        }

        return new NodeList(master, nodes);
    }

    private Node parseNode(String hostname, Map<String, Object> nodeConfig) {
        String jvmOpts = nodeConfig.containsKey(JVM_OPTS_KEY) ? (String) nodeConfig.get(JVM_OPTS_KEY) : null;
        @SuppressWarnings("unchecked")
        Map<String, String> javaProps = nodeConfig.containsKey(JAVA_PROPS_KEY) ? (Map<String, String>) nodeConfig
                .get(JAVA_PROPS_KEY) : null;
        @SuppressWarnings("unchecked")
        Map<String, String> envVars = nodeConfig.containsKey(ENV_VARS_KEY) ? (Map<String, String>) nodeConfig
                .get(ENV_VARS_KEY) : null;
        return new Node(hostname, jvmOpts, javaProps, envVars);
    }
    
    private MasterNode parseMasterNode(String hostname, Map<String, Object> nodeConfig) {
        Node node = parseNode(hostname, nodeConfig);
        String fqdn = nodeConfig.containsKey(MASTER_FQDN) ? (String) nodeConfig.get(MASTER_FQDN) : hostname;
        return new MasterNode(node, fqdn);
    }

}
