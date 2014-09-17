package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class ActiveDataChannel extends FtpDataChannel {

    private static final String     IP_PORT_FORMAT = "%d,%d,%d,%d,%d,%d";

    private volatile ServerSocket   mSocket;

    public ActiveDataChannel(IFtpDataChannelEvents listener) throws UnknownHostException, IOException {
        super(listener);
        mSocket = new ServerSocket(0);
    }

    public synchronized IpAndPort getIpAndFreePort() throws IOException {
        return new IpAndPort(InetAddress.getLocalHost().getAddress(), mSocket.getLocalPort());
    }

    @Override
    InputStream getInputStream() {
        try {
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
        return String.format(IP_PORT_FORMAT, 192,168,0,108, lhPortFragmnet, rhPortFragmnet);
    }

}
