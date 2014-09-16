package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FtpDataChannel implements Runnable {

    private static final String   FIND_IP_AND_PORT = "\\((\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?)\\)";
    private static final char     DOT              = '.';

    private Thread                mThread;
    private Socket                mSocket;
    private InputStream           mInputStream;
    private IFtpDataChannelEvents mEventHandler;

    public FtpDataChannel(String address, int port, IFtpDataChannelEvents eventHandler, OnConnectListener listener) throws UnknownHostException, IOException {
        mSocket = new Socket(address, port);
        mInputStream = mSocket.getInputStream();
        mEventHandler = eventHandler;
        listener.onConnect();
    }

    @Override
    public void run() {
        readStream();
    }

    public void start() {
        mThread = new Thread(this);
        mThread.start();
    }

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

    public void join() throws InterruptedException {
        mThread.join();
    }

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

    public interface OnConnectListener {
        void onConnect();
    }
}
