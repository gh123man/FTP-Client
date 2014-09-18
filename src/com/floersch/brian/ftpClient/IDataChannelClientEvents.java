package com.floersch.brian.ftpClient;

/**
 * Interface outlining events from an FTP data channel
 * 
 * @author brian
 */
public interface IDataChannelClientEvents {

    void onReady();

    void onChannelOpen();

}
