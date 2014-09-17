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

public class ActiveDataChannel extends FtpDataChannel {

    private static final String   IP_PORT_FORMAT = "%d,%d,%d,%d,%d,%d";

    private volatile ServerSocket mSocket;

    public ActiveDataChannel(IFtpDataChannelEvents listener) throws UnknownHostException, IOException {
        super(listener);
        mSocket = new ServerSocket(0);
    }

    public synchronized IpAndPort getIpAndFreePort() throws IOException {
        return new IpAndPort(getIpFromPrimaryNetworkInterface(), mSocket.getLocalPort());
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
        return String.format(IP_PORT_FORMAT, (int) 255 & ip[0], (int) 255 & ip[1], (int) 255 & ip[2], (int) 255 & ip[3], lhPortFragmnet, rhPortFragmnet);
    }

    public static byte[] getIpFromPrimaryNetworkInterface() {

        try {

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {

                NetworkInterface nextElement = networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = nextElement.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                        return address.getAddress();
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace(); // TODO
        }
        return null;

    }

}
