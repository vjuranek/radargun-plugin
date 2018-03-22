package org.jenkinsci.plugins.radargun.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.EnvVars;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.util.LogTaskListener;
import hudson.util.VariableResolver;

/**
 * Convenient class for resolving/expanding various variabales.
 * 
 * @author vjuranek
 * 
 */
public class Resolver {
    
    private static AbstractBuild<?, ?> build;
    private static EnvVars propertiesEnv;
    
    public static void init(final AbstractBuild<?, ?> build) {
        Resolver.build = build;
        Properties props = System.getProperties();
        Map<String, String> propMap = new HashMap<>(props.size());
        props.entrySet().forEach(entry -> propMap.put((String)entry.getKey(), (String)entry.getValue()));
        propertiesEnv = new EnvVars(propMap);
    }
    
    public static String doResolve(final String toResolve) throws IllegalStateException {
        if(build == null)
            throw new IllegalStateException("Variable resolver was not initialized!");
        
        return javaProps(buildVar(build, toResolve));
    }

    public static String buildVar(final AbstractBuild<?, ?> build,final String toResolve) {
        if(toResolve == null)
            return null;
        
        VariableResolver<String> vr = build.getBuildVariableResolver();
        String resolved = Util.replaceMacro(toResolve, vr);
        try {
            EnvVars env = build.getEnvironment(new LogTaskListener(LOG, Level.INFO));
            resolved = env.expand(resolved);
        } catch (Exception e) {
            //TODO no-op?
        }
        return resolved;
    }
    
    public static String javaProps(String toResolve) {
        if(toResolve == null)
            return null;
        String resolved = toResolve;
        
        try {
            resolved = propertiesEnv.expand(toResolve);
        } catch (Exception e) {
            //TODO no-op?
        }
        return resolved;
    }
    
    private static final Logger LOG = Logger.getLogger(Resolver.class.getName());
}
