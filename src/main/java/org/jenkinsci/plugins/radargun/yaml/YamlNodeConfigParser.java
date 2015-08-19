package org.jenkinsci.plugins.radargun.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jenkinsci.plugins.radargun.config.NodeConfigParser;
import org.jenkinsci.plugins.radargun.model.impl.MasterNode;
import org.jenkinsci.plugins.radargun.model.impl.Node;
import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.util.Resolver;
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
 * The contract is that the first node is master node. Master node can contain all element slave can contain, but
 * moreover master can specify also following options:
 * <ul>
 * <li>{@code fqdn} is master FQND or IP address. If not specified, master hostname is used.</li>
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

    public static final String LINE_SEP = System.getProperty("line.separator");
    public static final String EOF_REG_EXP = "\\A";
    public static final String INCLUDE_TAG = "!include";

    private final Yaml yaml;

    public YamlNodeConfigParser() {
        this.yaml = new Yaml();
    }

    @Override
    public NodeList parseNodeList(String nodesConfig) {
        String expandedConfig = expandIncludes(nodesConfig); // TODO solve in SnakeYAML or don't use includes at all! To
                                                             // be used really only as a last resort!

        @SuppressWarnings("unchecked")
        Map<String, Object> parsedConf = (Map<String, Object>) yaml.load(expandedConfig);
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

    private String expandIncludes(String orig) {
        StringBuilder sb = new StringBuilder();

        String[] lines = orig.split(LINE_SEP);
        for (String line : lines) {
            if (line.startsWith(INCLUDE_TAG)) {
                int fpStart = line.indexOf('"');
                if (fpStart < 0)
                    throw new IllegalArgumentException("String with file path (in quotes) expected");
                int fpEnd = line.indexOf('"', fpStart + 1);
                if (fpEnd < 0 || fpStart == fpEnd)
                    throw new IllegalArgumentException("String with file path (in quotes) expected");
                String filePath = line.substring(fpStart + 1, fpEnd);
                String filePathRes = Resolver.doResolve(filePath);
                sb.append(loadFile(filePathRes));
            } else {
                sb.append(line).append(LINE_SEP);
            }

        }
        return sb.toString();
    }

    private String loadFile(String filePath) {
        String content = null;
        File f = new File(filePath);
        try (InputStream is = new FileInputStream(f)) {
            Scanner s = new Scanner(is).useDelimiter(EOF_REG_EXP);
            content = s.hasNext() ? s.next() : null;
        } catch (IOException e) {
            throw new IllegalArgumentException("File to be included doesn't exists!", e);
        }
        return content;  //TODO expandIncludes(content)? Allow nested includes?
    }

}
