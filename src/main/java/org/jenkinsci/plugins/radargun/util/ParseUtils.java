package org.jenkinsci.plugins.radargun.util;

import java.util.HashMap;
import java.util.Map;

import org.jenkinsci.plugins.radargun.config.NodeConfigParser;
import org.jenkinsci.plugins.radargun.model.impl.NodeList;
import org.jenkinsci.plugins.radargun.yaml.YamlNodeConfigParser;

public class ParseUtils {

    public static NodeList parseNodeList(String nodeList) {
        NodeConfigParser parser = new YamlNodeConfigParser();
        return parser.parseNodeList(nodeList);
    }
    
    public static Map<String, String> mapToStringMap(final Object map) {
        if (!(map instanceof Map<?, ?>)) {
            throw new IllegalArgumentException(String.format("Cannot cast %s to Map<String, ?>", map.getClass().getName()));
        }
        
        Map<String, String> strMap = new HashMap<String, String>();
        for (Map.Entry<?, ?> e : ((Map<?, ?>)map).entrySet()) {
            strMap.put(e.getKey().toString(), e.getValue().toString());
        }
        return strMap;
    }
}
