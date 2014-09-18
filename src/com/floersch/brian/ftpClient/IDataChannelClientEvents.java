package com.floersch.brian.ftpClient;

/**
 * Interface outlining events from an FTP data channel
 * 
 * @author Brian Floersch (bpf4935@rit.edu)
 */
public interface IDataChannelClientEvents {

    /**
     * Called when the data channel is ready
     */
    void onReady();

    /**
     * called when the data channel is open
     */
    void onChannelOpen();

}
