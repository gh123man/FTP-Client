package com.floersch.brian.ftpChannels;

/**
 * Class maintaining an IP and Port pair
 * 
 * @author brian
 */
public class IpAndPort {

    /** Constants */
    private static final String IP_FORMAT = "%d.%d.%d.%d";

    /** Members */
    private byte[]              mRawIp;
    private String              mIp;
    private int                 mPort;

    /**
     * Constructor
     * 
     * @param Ip
     * @param port
     */
    public IpAndPort(String ip, int port) {
        mIp = ip;
        mPort = port;
    }

    public IpAndPort(byte[] ip, int port) {
        mRawIp = ip;
        mPort = port;
    }

    /**
     * returns the IP
     * 
     * @return
     */
    public String getIp() {
        if (mIp == null && mRawIp.length == 4) {
            return ipArrayToString(mRawIp);
        }
        return mIp;
    }

    public byte[] getRawIp() {
        if (mRawIp == null) {
            return ipStringToArray(mIp);
        }
        return mRawIp;
    }

    /**
     * returns the Port
     * 
     * @return
     */
    public int getPort() {
        return mPort;
    }

    public static String ipArrayToString(byte[] ip) {
        return String.format(IP_FORMAT, ip[0], ip[1], ip[2], ip[3]);
    }

    public static byte[] ipStringToArray(String ip) {
        String[] ipSplit = ip.split("\\.");
        return new byte[] { (byte) Integer.parseInt(ipSplit[0]), (byte) Integer.parseInt(ipSplit[1]), (byte) Integer.parseInt(ipSplit[2]), (byte) Integer.parseInt(ipSplit[3]) };
    }

}