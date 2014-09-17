package com.floersch.brian.ftpChannels;

/**
 * Interface outlining events from an FTP data channel
 * 
 * @author brian
 */
public interface IFtpDataChannelEvents {

    void onConnect(FtpDataChannel dataChannel);

    void onChannelOpen(FtpDataChannel dataChannel);

}
