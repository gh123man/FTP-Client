package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class ActiveDataChannel extends FtpDataChannel {

    private static final String     IP_PORT_FORMAT = "%d,%d,%d,%d,%d,%d";

    private volatile ServerSocket   mSocket;
    private volatile OnReadyListner mOnReadyListner;

    public static ActiveDataChannel startNewActiveDataChannelThread(IFtpDataChannelEvents eventHandler, OnReadyListner listener) throws UnknownHostException, IOException {
        ActiveDataChannel dataChannel = new ActiveDataChannel(eventHandler, listener);
        dataChannel.start();
        return dataChannel;
    }

    private ActiveDataChannel(IFtpDataChannelEvents eventHandler, OnReadyListner listener) throws UnknownHostException, IOException {
        super(eventHandler);
        mOnReadyListner = listener;
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
            mOnReadyListner.onReady();
            return mSocket.accept().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();// TODO
        }
        return null;
    }

    public static String encodeIpAndPort(IpAndPort ipAndPort) {
        byte[] ip = ipAndPort.getRawIp();
        int lhPortFragmnet = (int) Math.floor(ipAndPort.getPort() / 256);
        int rhPortFragmnet = ipAndPort.getPort() % 256;
        System.out.println(ipAndPort.getPort());
        return String.format(IP_PORT_FORMAT, ip[0], ip[1], ip[2], ip[3], 181, 130);
    }

    public interface OnReadyListner {
        void onReady();
    }

}
