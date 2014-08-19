package org.jenkinsci.plugins.radargun;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.framework.io.LargeText;

import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Action;

public class RadarGunNodeAction implements Action {
    
    private static final String DEFAULT_ACTION_NAME = "RadarGun node ";
    
    private final AbstractBuild<?, ?> build;
    private final String hostname;
    private final String actionName;
    private boolean inProgress;
    
    
    public RadarGunNodeAction(AbstractBuild<?, ?> build, String hostname) {
        this.build = build;
        this.hostname = hostname;
        this.actionName = DEFAULT_ACTION_NAME;
        this.inProgress = false;
    }
    
    public RadarGunNodeAction(AbstractBuild<?, ?> build, String hostname, String actionName) {
        this.build = build;
        this.hostname = hostname;
        this.actionName = actionName;
        this.inProgress = false;
    }
    
    public AbstractBuild<?, ?> getBuild() {
        return build;
    }
    
    public String getHostname() {
        return hostname;
    }
    
    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }
    
    public String getIconFileName() {
        return "/plugin/radargun/icons/radargun24.png";
    }

    public String getDisplayName() {
        return actionName + hostname;
    }

    public String getUrlName() {
        return "console-" + Util.rawEncode(hostname);
    }
    
    // required by index.jelly
    public AbstractBuild<?,?> getOwnerBuild(){
        return build;
    }
    
    // required by consoleText.jelly
    public Reader getLogReader() throws IOException {
        return new InputStreamReader(getLogInputStream());
    }
    
    public InputStream getLogInputStream() throws IOException {
        File logFile = getLogFile();
        
        if (logFile != null && logFile.exists() ) {
            FileInputStream fis = new FileInputStream(logFile);
            if (logFile.getName().endsWith(".gz")) {
                return new GZIPInputStream(fis);
            } else {
                return fis;
            }
        }
        
        String message = "No such file: " + logFile;
        return new ByteArrayInputStream(message.getBytes());
    }
    
    public void doProgressiveLog(StaplerRequest req, StaplerResponse rsp) throws IOException {
        new LargeText(getLogFile(), !inProgress).doProgressText(req, rsp);
    }
    
    public File getLogFile() {
        String logFileName = hostname;
        File gzipLogFile = new File(build.getRootDir(), logFileName + ".log.gz");
        if(gzipLogFile.isFile())
            return gzipLogFile;
        return new File(build.getRootDir(), logFileName + ".log");
    }

}
