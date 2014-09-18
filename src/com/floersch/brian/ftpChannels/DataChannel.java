package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for handling an FTP data channel
 * 
 * @author brian
 */
public abstract class DataChannel {

    /** Members */
    private volatile InputStream mInputStream;

    abstract InputStream getInputStream();

    public void openStream() {
        mInputStream = getInputStream();
    }

    /**
     * reads the data stream from the server
     */
    public void copyStream(OutputStream outStream) {
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = mInputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            mInputStream.close();
        } catch (IOException e) {
            e.printStackTrace(); // TODO
        }

    }

}
