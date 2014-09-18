package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Class for handling an active FTP data channel
 * 
 * @author Brian Floersch (bpf4935@rit.edu)
 */
public class ActiveDataChannel extends DataChannel {

    /** Constants */
    private static final String IP_PORT_FORMAT = "%d,%d,%d,%d,%d,%d";

    /** Members */
    private ServerSocket        mSocket;

    /**
     * Constructor
     * 
     * @throws UnknownHostException
     * @throws IOException
     */
    public ActiveDataChannel() throws UnknownHostException, IOException {
        mSocket = new ServerSocket(0);
    }

    /**
     * gets the current IP for usage and a free port.
     * 
     * @return
     * @throws IOException
     */
    public IpAndPort getIpAndFreePort() throws IOException {
        return new IpAndPort(getIpFromPrimaryNetworkInterface(), mSocket.getLocalPort());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.floersch.brian.ftpChannels.DataChannel#getInputStream()
     */
    @Override
    InputStream getInputStream() throws IOException {
        return mSocket.accept().getInputStream();
    }

    /**
     * Encodes an IP and port combo into the FTP format
     * 
     * @param ipAndPort
     * @return
     */
    public static String encodeIpAndPort(IpAndPort ipAndPort) {
        byte[] ip = ipAndPort.getRawIp();
        int lhPortFragmnet = (int) Math.floor(ipAndPort.getPort() / 256);
        int rhPortFragmnet = ipAndPort.getPort() % 256;
        return String.format(IP_PORT_FORMAT, b2i(ip[0]), b2i(ip[1]), b2i(ip[2]), b2i(ip[3]), lhPortFragmnet, rhPortFragmnet);
    }
    
    /**
     * Helper method to assist normalizing an IP
     * @param b
     * @return
     */
    private static int b2i(byte b) {
        return (int) 255 & b;
    }

    /**
     * Gets the current IP for the primary network interface.
     * This will get the best IP if you are behind a router/firewall.
     * 
     * @return
     * @throws SocketException
     * @throws UnknownHostException 
     */
    public static byte[] getIpFromPrimaryNetworkInterface() throws SocketException, UnknownHostException {

        byte[] finalAddress = null;
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {

            NetworkInterface nextElement = networkInterfaces.nextElement();
            Enumeration<InetAddress> addresses = nextElement.getInetAddresses();

            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address instanceof Inet4Address && !address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                    finalAddress = address.getAddress();
                }
            }
        }
        
        //Fall back to local address if no other addresses are found
        if (finalAddress == null) {
            finalAddress = InetAddress.getLocalHost().getAddress();
        }
        return finalAddress;
    }

}
