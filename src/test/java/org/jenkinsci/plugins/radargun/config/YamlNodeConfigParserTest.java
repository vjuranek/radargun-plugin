package org.jenkinsci.plugins.radargun.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Map;

import org.jenkinsci.plugins.radargun.model.impl.Node;
import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.util.IOUtils;
import org.jenkinsci.plugins.radargun.util.ParseUtils;
import org.junit.Test;

public class YamlNodeConfigParserTest {

    @Test
    public void testNodeConfigParser() throws IOException {
        String config = IOUtils.loadResourceAsString("testNodeConfig.yaml");
        
        assertNotNull("Unable to load test YAML config file", config);
        NodeList nodes = ParseUtils.parseNodeList(config);
        assertEquals(2, nodes.getNodes().size());
        
        Node master = nodes.getMaster();
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
