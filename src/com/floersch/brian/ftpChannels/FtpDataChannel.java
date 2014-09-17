package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;


/**
 * Class for handling an FTP data channel
 * 
 * @author brian
 */
public abstract class FtpDataChannel {

    /** Members */
    private volatile InputStream        mInputStream;
    private final IFtpDataChannelEvents mListener;

    protected FtpDataChannel(IFtpDataChannelEvents listener) throws UnknownHostException, IOException {
        mListener = listener;
    }

    abstract InputStream getInputStream();

    public void initlize() {
        mListener.onConnect(this);
        mInputStream = getInputStream();
    }
    
    public void onChannelOpen() {
        mListener.onChannelOpen(this);
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
        } catch (IOException e) {
            e.printStackTrace(); // handle better
        }

    }
    
}
