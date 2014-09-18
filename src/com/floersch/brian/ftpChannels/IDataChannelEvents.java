package com.floersch.brian.ftpChannels;

/**
 * Interface outlining events from an FTP data channel
 * 
 * @author brian
 */
public interface IDataChannelEvents {

    void onConnect(DataChannel dataChannel);

    void onChannelOpen(DataChannel dataChannel);

}
