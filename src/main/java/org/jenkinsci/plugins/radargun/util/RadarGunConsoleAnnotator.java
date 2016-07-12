package org.jenkinsci.plugins.radargun.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import hudson.console.LineTransformationOutputStream;

/**
 * 
 * @author vjuranek
 *
 */
public class RadarGunConsoleAnnotator extends LineTransformationOutputStream {

    private final OutputStream out;
    
    public RadarGunConsoleAnnotator(OutputStream out) {
        this.out = out;
    }
    
    @Override
    protected void eol(byte[] b, int len) throws IOException {
        String line = Charset.defaultCharset().decode(ByteBuffer.wrap(b, 0, len)).toString();
        if (line.startsWith("[RadarGun]") || line.contains("[org.radargun")) {
            new RadarGunConsoleNote().encodeTo(out);
        }
        out.write(b,0,len);
    }

    @Override
    public void close() throws IOException {
        super.close();
        out.close();
    }

}