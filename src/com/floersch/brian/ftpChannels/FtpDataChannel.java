package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for handling an FTP data channel
 * 
 * @author brian
 */
public class FtpDataChannel implements Runnable {

    /** constants */
    private static final String   FIND_IP_AND_PORT = "\\((\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?)\\)";
    private static final char     DOT              = '.';

    /** Members */
    private Thread                mThread;
    private Socket                mSocket;
    private InputStream           mInputStream;
    private IFtpDataChannelEvents mEventHandler;

    /**
     * Constructor
     * 
     * @param address
     * @param port
     * @param eventHandler
     * @throws UnknownHostException
     * @throws IOException
     */
    public FtpDataChannel(String address, int port, IFtpDataChannelEvents eventHandler) throws UnknownHostException, IOException {
        mSocket = new Socket(address, port);
        mInputStream = mSocket.getInputStream();
        mEventHandler = eventHandler;
        mEventHandler.onConnect();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        readStream();
    }

    /**
     * Starts the thread
     */
    public void start() {
        mThread = new Thread(this);
        mThread.start();
    }

    /**
     * reads the data stream from the server
     */
    private void readStream() {
        int b;
        try {
            while ((b = mInputStream.read()) != -1) {
                mEventHandler.writeByte(b);
            }
        } catch (IOException e) {
            e.printStackTrace(); // handle better
        }

    }

    /**
     * Calls Thread.join()
     * 
     * @throws InterruptedException
     */
    public void join() throws InterruptedException {
        mThread.join();
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
                    ip += m.group(i) + DOT;
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
