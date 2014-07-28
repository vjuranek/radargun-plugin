package org.jenkinsci.plugins.radargun;

/**
 * RadarGunExecutable
 * 
 * @author vjuranek
 *
 */
public enum RadarGunExecutable {

    CLEAN("clean.sh"),
    DIST("dist.sh"),
    LOCAL("local.sh"),
    MASTER("master.sh"),
    SLAVE("slave.sh");
    
    private String fileName;
    
    private RadarGunExecutable(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileName() {
        return fileName;
    }
    
}
