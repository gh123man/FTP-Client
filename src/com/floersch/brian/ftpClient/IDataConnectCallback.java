package com.floersch.brian.ftpClient;

/**
 * Callback for adding a callback to the FTP state for when a data connection is
 * established.
 * 
 * @author brian
 */
public interface IDataConnectCallback {

    /**
     * Called by the implementer to run the callback
     */
    void onConnect();
}
