package org.jenkinsci.plugins.radargun.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Map;

import org.jenkinsci.plugins.radargun.model.impl.MasterNode;
import org.jenkinsci.plugins.radargun.model.impl.Node;
import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.util.ParseUtils;
import org.junit.Test;

public class ParseUtilsTest {

    @Test
    public void testParseNodeList() throws IOException {
        String config = IOUtils.loadResourceAsString("testNodeConfig.yaml");
        
        assertNotNull("Unable to load test YAML config file", config);
        NodeList nodes = ParseUtils.parseNodeList(config);
        assertEquals(2, nodes.getNodes().size());
        
        MasterNode master = (MasterNode) nodes.getMaster();
        assertEquals("172.12.0.8", master.getFqdn());
        assertEquals("edg-perf08", master.getHostname());
        assertEquals("-server -Xms8g -Xmx8g -XX:+UseLargePages", master.getJvmOptions());
        Map<String, String> envVars = master.getEnvVars();
        assertNotNull(envVars);
        assertEquals("172.12.0.8", envVars.get("jgroups.udp.mcast_addr"));
        assertEquals("172.12.0.1", envVars.get("infinispan_server1_address"));
        Map<String, String> javaProps = master.getJavaProps();
        assertNotNull(javaProps);
        assertEquals("192.168.117.12:7800;192.168.117.13:7800;192.168.117.14:7800;", javaProps.get("site.default_site.tcp"));
        
        assertEquals(1, nodes.getSlaveCount());
        Node slave = nodes.getNodes().get(1);
        assertEquals("edg-perf01", slave.getHostname());
        assertNull(slave.getJvmOptions());
        envVars = slave.getEnvVars();
        assertNotNull(envVars);
        assertEquals("172.12.0.1", envVars.get("jgroups.udp.mcast_addr"));
        assertEquals("172.12.0.1", envVars.get("infinispan_server1_address"));
        javaProps = slave.getJavaProps();
        assertNotNull(javaProps);
        assertEquals("192.168.117.12:7800;192.168.117.13:7800;192.168.117.14:7800;", javaProps.get("site.default_site.tcp"));
    }
}
