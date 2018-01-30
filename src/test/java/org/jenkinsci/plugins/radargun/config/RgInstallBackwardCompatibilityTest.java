package org.jenkinsci.plugins.radargun.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jenkinsci.plugins.radargun.RadarGunBuilder;
import org.jenkinsci.plugins.radargun.RadarGunInstallation;
import org.jenkinsci.plugins.radargun.util.Functions;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;

import hudson.model.FreeStyleProject;
import hudson.tasks.Builder;

public class RgInstallBackwardCompatibilityTest {
    
    @Rule public JenkinsRule rule = new JenkinsRule();
    
    @Test
    @LocalData
    public void testRgInstallations() throws Exception {
        List<RadarGunInstallation> installs = rule.jenkins.getDescriptorByType(RadarGunBuilder.DescriptorImpl.class).getInstallations(); 
        assertEquals(3, installs.size());
        assertEquals("rg-snapshot", installs.get(0).getName());
        assertEquals("/tmp/radarun/RadarGun-SNAPSHOT", installs.get(0).getHome());
    }

    @Test
    @LocalData
    public void testRgInstallInJobConfig() throws Exception {
        assertTrue(rule.jenkins.getJobNames().contains("backwardComp"));
        FreeStyleProject job = (FreeStyleProject)rule.jenkins.getItem("backwardComp");
        List<Builder> builders = job.getBuilders();
        assertEquals(1, builders.size());
        assertTrue(builders.get(0) instanceof RadarGunBuilder);
        RadarGunBuilder rgBuilder = (RadarGunBuilder)builders.get(0);
        assertEquals("rg-snapshot", rgBuilder.getRadarGunInstance().getName());
        assertTrue(rgBuilder.getRadarGunInstance() instanceof RadarGunInstallationWrapper);
        assertEquals("/tmp/radarun/RadarGun-SNAPSHOT", Functions.getRgInstallation(rgBuilder.getRadarGunInstance()).getHome());
    }
    
}
