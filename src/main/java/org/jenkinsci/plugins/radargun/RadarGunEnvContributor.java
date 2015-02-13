package org.jenkinsci.plugins.radargun;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.TaskListener;
import hudson.model.EnvironmentContributor;
import hudson.model.Run;

import java.io.IOException;
import java.util.List;

@Extension
public class RadarGunEnvContributor extends EnvironmentContributor {
    
    public static final String RADARGUN_HOME = "RADARGUN_HOME";
    
    @Override
    public void buildEnvironmentFor(@SuppressWarnings("rawtypes") Run r, EnvVars envs, TaskListener listener)
            throws IOException, InterruptedException {

        List<RadarGunInvisibleAction> envActions = r.getActions(RadarGunInvisibleAction.class);
        if (envActions.size() == 0) {
            return;
        }

        envs.put(RADARGUN_HOME, envActions.get(0).getRgHome());
    }

}
