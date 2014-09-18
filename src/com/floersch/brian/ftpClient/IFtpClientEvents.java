package com.floersch.brian.ftpClient;

/**
 * Interface for handling FTP client events
 * 
 * @author Brian Floersch (bpf4935@rit.edu)
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

    /**
     * handles an exception
     * @param e
     */
    void onExceptionThrown(Exception e);
}
