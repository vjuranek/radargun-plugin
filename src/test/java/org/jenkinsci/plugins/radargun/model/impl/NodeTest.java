package org.jenkinsci.plugins.radargun.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class NodeTest {

    @Test
    public void testAllJavaOpts() {
        Map<String, String> javaProps = new HashMap<>();
        javaProps.put("site.default_site.tcp", "192.168.117.12:7800;192.168.117.13:7800;192.168.117.14:7800;");
        javaProps.put("site.default_site.udp", "192.168.117.12:52000;192.168.117.13:52000;192.168.117.14:52000;");
        Map<String, String> envVars = new HashMap<>();
        envVars.put("infinispan_server1_address", "172.12.0.1");
        Node node = new Node("test_hostname", "-server -Xms8g -Xmx8g -XX:+UseLargePages", javaProps, envVars);
        assertNotNull(node.getAllJavaOpts());
        assertEquals(
                " '-server -Xms8g -Xmx8g -XX:+UseLargePages -Dsite.default_site.udp=192.168.117.12:52000;192.168.117.13:52000;192.168.117.14:52000; -Dsite.default_site.tcp=192.168.117.12:7800;192.168.117.13:7800;192.168.117.14:7800; '",
                node.getAllJavaOpts());
    }
    
    @Test
    public void testJavaOptsOnly() {
        Map<String, String> javaProps = new HashMap<>();
        javaProps.put("site.default_site.tcp", "192.168.117.12:7800;192.168.117.13:7800;192.168.117.14:7800;");
        javaProps.put("site.default_site.udp", "192.168.117.12:52000;192.168.117.13:52000;192.168.117.14:52000;");
        Map<String, String> envVars = new HashMap<>();
        envVars.put("infinispan_server1_address", "172.12.0.1");
        Node node = new Node("test_hostname", null, javaProps, envVars);
        assertNotNull(node.getAllJavaOpts());
        assertEquals(
                " '-Dsite.default_site.udp=192.168.117.12:52000;192.168.117.13:52000;192.168.117.14:52000; -Dsite.default_site.tcp=192.168.117.12:7800;192.168.117.13:7800;192.168.117.14:7800; '",
                node.getAllJavaOpts());
    }
    
    @Test
    public void testJvmOptsOnly() {
        Map<String, String> envVars = new HashMap<>();
        envVars.put("infinispan_server1_address", "172.12.0.1");
        Node node = new Node("test_hostname", "-server -Xms8g -Xmx8g -XX:+UseLargePages", null, envVars);
        assertNotNull(node.getAllJavaOpts());
        assertEquals(" '-server -Xms8g -Xmx8g -XX:+UseLargePages '", node.getAllJavaOpts());
    }
}
