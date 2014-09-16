package com.floersch.brian.ftpChannels;

/**
 * Class maintaining an IP and Port pair
 * 
 * @author brian
 */
public class IpAndPort {

    /** Members */
    private String mIp;
    private int    mPort;

    /**
     * Constructor
     * 
     * @param Ip
     * @param port
     */
    public IpAndPort(String Ip, int port) {
        mIp = Ip;
        mPort = port;
    }

    /**
     * returns the IP
     * 
     * @return
     */
    public String getIp() {
        return mIp;
    }

    /**
     * returns the Port
     * 
     * @return
     */
    public int getPort() {
        return mPort;
    }

}