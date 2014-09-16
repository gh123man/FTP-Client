package com.floersch.brian.ftpClient;

/**
 * Interface for handling Ftp client events
 * 
 * @author brian
 */
public interface IFtpClientEvents {

    /**
     * sends a string to the handler
     * 
     * @param string
     */
    void print(String string);

    /**
     * sends a string requesting a newline to the handler
     * 
     * @param string
     */
    void println(String string);

    /**
     * gets input from the handler
     * 
     * @return
     */
    String requestInput();
}
