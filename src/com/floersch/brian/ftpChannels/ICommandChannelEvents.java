package com.floersch.brian.ftpChannels;

/**
 * Interface outlining events from an FTP command channel
 * 
 * @author Brian Floersch (bpf4935@rit.edu)
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

    /**
     * handles a debug message
     * @param message
     */
    void onDebugMessage(String message);

}
