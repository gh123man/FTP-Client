package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for handling a passive FTP data channel
 * 
 * @author Brian Floersch (bpf4935@rit.edu)
 */
public class PassiveDataChannel extends DataChannel {

    /** constants */
    private static final String FIND_IP_AND_PORT = "\\((\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?)\\)";

    /** Members */
    private Socket              mSocket;
    private final IpAndPort     mIpAndPort;

    /**
     * Constructor.
     * @param ipAndPort
     * @throws UnknownHostException
     * @throws IOException
     */
    public PassiveDataChannel(IpAndPort ipAndPort) throws UnknownHostException, IOException {
        mIpAndPort = ipAndPort;
        mSocket = new Socket(mIpAndPort.getIp(), mIpAndPort.getPort());
    }

    /*
     * (non-Javadoc)
     * @see com.floersch.brian.ftpChannels.DataChannel#getInputStream()
     */
    @Override
    InputStream getInputStream() throws IOException {
        return mSocket.getInputStream();
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
