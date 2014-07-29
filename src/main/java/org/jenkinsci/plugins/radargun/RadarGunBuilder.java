package org.jenkinsci.plugins.radargun;

import hudson.CopyOnWrite;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class RadarGunBuilder extends Builder {

    private String radarGunName;
    
    @DataBoundConstructor
    public RadarGunBuilder(String radarGunName) {
        this.radarGunName = radarGunName;
    }
    
    public String getRadarGunName() {
        return radarGunName;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        RadarGunInstallation rgInstall = getDescriptor().getInstallation(radarGunName);
        //TODO check for null rgInstall
        String f = rgInstall.getExecutable(RadarGunExecutable.LOCAL, launcher.getChannel());
        System.out.println("Starting " + f);
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @CopyOnWrite
        private volatile List<RadarGunInstallation> installations = new ArrayList<RadarGunInstallation>();

        public DescriptorImpl() {
            load();
        }

        public List<RadarGunInstallation> getInstallations() {
            return installations;
        }

        public void setInstallations(RadarGunInstallation... installations) {
            this.installations = new ArrayList<RadarGunInstallation>();
            for (RadarGunInstallation installation : installations) {
                this.installations.add(installation);
            }
        }

        public RadarGunInstallation getInstallation(String installationName) {
            if (installationName == null || installationName.isEmpty())
                return null;

            for (RadarGunInstallation i : installations) {
                if (i.getName().equals(installationName))
                    return i;
            }
            return null;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Run RadarGun";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }
        
        public ListBoxModel doFillRadarGunNameItems() {
            ListBoxModel lb = new ListBoxModel();
            for (RadarGunInstallation rgi : installations) {
                lb.add(rgi.getName(), rgi.getName());
            }
            return lb;
        }

    }
}
