package com.floersch.brian.ftpChannels;

/**
 * Interface outlining events from an FTP command channel
 * 
 * @author brian
 */
public interface ICommandChannelEvents {

    /**
     * Notifies the end of a response
     * 
     * @param code
     * @param response
     */
    void onResponse(int code, String response);

    /**
     * Notifies a disconnect
     */
    void onDisconnect();

}
