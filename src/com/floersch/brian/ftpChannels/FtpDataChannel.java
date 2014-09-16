package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

/**
 * Class for handling an FTP data channel
 * 
 * @author brian
 */
public abstract class FtpDataChannel implements Runnable {

    /** Members */
    private Thread                      mThread;
    private volatile InputStream           mInputStream;
    private final IFtpDataChannelEvents mEventHandler;

    protected FtpDataChannel(IFtpDataChannelEvents eventHandler) throws UnknownHostException, IOException {
        mEventHandler = eventHandler;
    }
    
    abstract void connect();
    abstract InputStream getInputStream();
    

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        connect();
        initlize();
        readStream();
    }
    
    protected void initlize() {
        mInputStream = getInputStream();
        mEventHandler.onConnect();
    }

    /**
     * Starts the thread
     */
    public void start() {
        mThread = new Thread(this);
        mThread.start();
    }

    /**
     * reads the data stream from the server
     */
    private void readStream() {
        int b;
        try {
            while ((b = mInputStream.read()) != -1) {
                mEventHandler.writeByte(b);
            }
        } catch (IOException e) {
            e.printStackTrace(); // handle better
        }

    }

    /**
     * Calls Thread.join()
     * 
     * @throws InterruptedException
     */
    public void join() throws InterruptedException {
        mThread.join();
    }

}
