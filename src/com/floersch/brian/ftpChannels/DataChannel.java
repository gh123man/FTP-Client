package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for handling an FTP data channel
 * 
 * @author Brian Floersch (bpf4935@rit.edu)
 */
public abstract class DataChannel {

    /** Members */
    private InputStream mInputStream;

    /**
     * Gets the active input stream
     * @return
     * @throws IOException
     */
    abstract InputStream getInputStream() throws IOException;

    /**
     * Opens the input stream
     * @throws IOException
     */
    public void openStream() throws IOException {
        mInputStream = getInputStream();
    }

    /**
     * reads the data stream from the server
     * @throws IOException 
     */
    public void copyStream(OutputStream outStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = mInputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        mInputStream.close();

    }

}
