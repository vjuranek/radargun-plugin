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
        @SuppressWarnings("unchecked")
        Map<String, ?> strKeyMap = (Map<String, ?>)map;
        Map<String, String> strMap = new HashMap<String, String>();
        for (String key : strKeyMap.keySet()) {
            strMap.put(key, strKeyMap.get(key).toString());
        }
        return strMap;
    }
}
