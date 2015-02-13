package org.jenkinsci.plugins.radargun;

import hudson.model.InvisibleAction;

public class RadarGunInvisibleAction extends InvisibleAction {
    
    private String rgHome;
    
    public RadarGunInvisibleAction() {
        
    }

    public RadarGunInvisibleAction(String rgHome) {
        this.rgHome = rgHome;
    }
    
    public String getRgHome() {
        return rgHome;
    }
}
