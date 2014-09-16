package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PassiveDataChannel extends FtpDataChannel {

    /** constants */
    private static final String         FIND_IP_AND_PORT = "\\((\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?)\\)";
    
    private Socket mSocket;
    private final IpAndPort mIpAndPort;
    
    
    public static PassiveDataChannel startNewPassiveDataChannel(IpAndPort ipAndPort, IFtpDataChannelEvents eventHandler) throws UnknownHostException, IOException {
        PassiveDataChannel dataChannel = new PassiveDataChannel(ipAndPort, eventHandler);
        dataChannel.start();
        return dataChannel;
    }
    
    private PassiveDataChannel(IpAndPort ipAndPort, IFtpDataChannelEvents eventHandler) throws UnknownHostException, IOException {
        super(eventHandler);
        mIpAndPort = ipAndPort;
        
    }
    
    @Override
    void connect() {
        try {
            mSocket = new Socket(mIpAndPort.getIp(), mIpAndPort.getPort());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    InputStream getInputStream() {
        try {
            return mSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        }
        return null;
    }
    
    /**
     * Decodes a string containing passive IP and Port
     * 
     * @param input
     * @return
     */
    public static IpAndPort decodePasv(String input) {

        Pattern p = Pattern.compile(FIND_IP_AND_PORT);
        Matcher m = p.matcher(input);

        if (m.find()) {
            String ip = "";
            int port = 0;
            for (int i = 1; i <= m.groupCount(); i++) {
                if (i < 4) {
                    ip += m.group(i) + '.';
                } else if (i == 4) {
                    ip += m.group(i);
                } else if (i == 5) {
                    port = Integer.parseInt(m.group(i));
                    port *= 256;
                } else if (i == 6) {
                    port += Integer.parseInt(m.group(i));
                }
            }
            return new IpAndPort(ip, port);

        }
        return null;
    }

}
