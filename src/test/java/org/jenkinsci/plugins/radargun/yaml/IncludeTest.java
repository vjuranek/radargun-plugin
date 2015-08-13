package org.jenkinsci.plugins.radargun.yaml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Map;

import org.jenkinsci.plugins.radargun.config.NodeConfigParser;
import org.jenkinsci.plugins.radargun.model.impl.MasterNode;
import org.jenkinsci.plugins.radargun.model.impl.Node;
import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.util.IOUtils;
import org.junit.Test;

public class IncludeTest {
    
    @Test
    public void testParseNodeList() throws IOException {
        String config = IOUtils.loadResourceAsString("include.yaml");
        assertNotNull("Unable to load test YAML config file", config);
        
        // add include tag into original config file
        String includedPath = IOUtils.getAbsoluteResourcePath("default.yaml");
        assertNotNull("Unable to locate included YAML file", includedPath);
        StringBuilder sb = new StringBuilder("---");
        sb.append(YamlNodeConfigParser.LINE_SEP);
        sb.append(YamlNodeConfigParser.INCLUDE_TAG).append(" \"").append(includedPath).append("\"").append(YamlNodeConfigParser.LINE_SEP);
        sb.append(config);
        
        NodeConfigParser parser = new YamlNodeConfigParser();
        NodeList nodes = parser.parseNodeList(sb.toString());
        assertEquals(2, nodes.getNodes().size());
        
        MasterNode master = (MasterNode) nodes.getMaster();
        assertEquals("172.12.0.8", master.getFqdn());
        assertEquals("edg-perf08", master.getHostname());
        assertEquals("-server -Xms8g -Xmx8g -XX:+UseLargePages", master.getJvmOptions());
        Map<String, String> envVars = master.getEnvVars();
        assertNotNull(envVars);
        assertEquals("172.12.0.8", envVars.get("jgroups.udp.mcast_addr"));
        assertEquals("172.12.0.1", envVars.get("infinispan_server1_address"));
        
        assertEquals(1, nodes.getSlaveCount());
        Node slave = nodes.getNodes().get(1);
        assertEquals("edg-perf01", slave.getHostname());
        assertNull(slave.getJvmOptions());
        envVars = slave.getEnvVars();
        assertNotNull(envVars);
        assertEquals("172.12.0.1", envVars.get("jgroups.udp.mcast_addr"));
        assertEquals("172.12.0.1", envVars.get("infinispan_server1_address"));
    }

}
