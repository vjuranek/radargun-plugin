package org.jenkinsci.plugins.radargun;

import hudson.Extension;
import hudson.tools.DownloadFromUrlInstaller;
import hudson.tools.ToolInstallation;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * RadarGunInstaller
 * 
 * @author vjuranek
 *
 */
public class RadarGunInstaller extends DownloadFromUrlInstaller {
    
    @DataBoundConstructor                                                                                                                      
    public RadarGunInstaller(String id) {                                                                                                        
        super(id);                                                                                                                             
    }                                                                                                                                          
                                                                                                                                               
    @Extension                                                                                                                                 
    public static final class DescriptorImpl extends DownloadFromUrlInstaller.DescriptorImpl<RadarGunInstaller> {                                
        public String getDisplayName() {                                                                                                       
            return "Install from custom URL";                                                                                                  
        }                                                                                                                                      
                                                                                                                                               
        @Override                                                                                                                              
        public boolean isApplicable(Class<? extends ToolInstallation> toolType) {                                                              
            return toolType == RadarGunInstallation.class;                                                                                       
        }                                                                                                                                      
    }                                                                                                                                          
}    