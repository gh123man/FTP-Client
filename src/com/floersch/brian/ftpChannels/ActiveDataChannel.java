package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class ActiveDataChannel extends FtpDataChannel {

    private static final String         IP_PORT_FORMAT   = "%d,%d,%d,%d,%d,%d";
    
    private volatile ServerSocket mSocket;
    
    public static ActiveDataChannel startNewActiveDataChannelThread(IFtpDataChannelEvents eventHandler) throws UnknownHostException, IOException {
        ActiveDataChannel dataChannel = new ActiveDataChannel(eventHandler);
        dataChannel.start();
        return dataChannel;
    }
    
    private ActiveDataChannel(IFtpDataChannelEvents eventHandler) throws UnknownHostException, IOException {
        super(eventHandler);
    }
    
    @Override
    void connect() {
        try {
            mSocket = new ServerSocket(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    public synchronized IpAndPort getIpAndFreePort() throws IOException {
        return new IpAndPort(InetAddress.getLocalHost().getAddress(), mSocket.getLocalPort());
    }
    
    @Override
    InputStream getInputStream() {
        try {
            return mSocket.accept().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();//TODO
        }
        return null;
    }
    
    
    public static String encodeIpAndPort(IpAndPort ipAndPort) {
        byte[] ip = ipAndPort.getRawIp();
        int rhPortFragmnet = (int) Math.floor(ipAndPort.getPort() / 256);
        int lhPortFragmnet = ipAndPort.getPort() % 256;
        System.out.println(ipAndPort.getPort());
        return String.format(IP_PORT_FORMAT, ip[0], ip[1], ip[2], ip[3], rhPortFragmnet, lhPortFragmnet);
    }


}
