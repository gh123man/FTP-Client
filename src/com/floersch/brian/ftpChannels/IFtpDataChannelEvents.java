package com.floersch.brian.ftpChannels;

/**
 * Interface outlining events from an FTP data channel
 * 
 * @author brian
 */
public interface IFtpDataChannelEvents {

    /**
     * writes one byte to the handler
     * 
     * @param b
     */
    void writeByte(int b);

    /**
     * Notifies on connect
     */
    void onConnect();

}
