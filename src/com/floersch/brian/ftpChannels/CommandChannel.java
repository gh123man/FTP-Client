package com.floersch.brian.ftpChannels;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class for handling an FTP Command channel connection
 * 
 * @author brian
 */
public class CommandChannel {

    /** response codes */
    public static final int             ABOUT_TO_OPEN_DATA_CHANNEL = 150;
    public static final int             SUCCESS                    = 200;
    public static final int             READY_FOR_NEW_USER         = 220;
    public static final int             CLOSING_DATA_CHANNEL       = 226;
    public static final int             ENTERING_PASSIVE_MODE      = 227;
    public static final int             USER_LOGGED_IN             = 230;
    public static final int             UNAME_OK_NEED_PASS         = 331;
    public static final int             CANNOT_OPEN_DATA_CHANNEL   = 425;
    public static final int             NOT_LOGGED_IN              = 530;
    public static final int             FILE_NOT_FOUND             = 550;

    /** commands */
    private static final String         CMD_FORMAT                 = "%s\r\n";
    private static final String         USER                       = "USER %s";
    private static final String         PASS                       = "PASS %s";
    private static final String         PASSIVE                    = "PASV";
    private static final String         LIST                       = "LIST";
    private static final String         TYPE                       = "TYPE %s";
    private static final String         I                          = "I";
    private static final String         A                          = "A";
    private static final String         CDUP                       = "CDUP";
    private static final String         QUIT                       = "QUIT";
    private static final String         PWD                        = "PWD";
    private static final String         CD                         = "CWD %s";
    private static final String         PORT                       = "PORT %s";
    private static final String         RETRIEVE                   = "RETR %s";

    /** Members */
    private final Socket                mSocket;
    private final InputStream           mInputStream;
    private final PrintStream           mWriter;
    private final ICommandChannelEvents mEventHandler;
    private boolean                     mConnected;
    private boolean                     mDebug;

    /**
     * Constructor.
     * 
     * @param address
     * @param port
     * @param eventHandler
     * @throws UnknownHostException
     * @throws IOException
     */
    public CommandChannel(String address, int port, ICommandChannelEvents eventHandler) throws UnknownHostException, IOException {
        mSocket = new Socket(address, port);
        mWriter = new PrintStream(mSocket.getOutputStream());
        mInputStream = mSocket.getInputStream();
        mConnected = true;
        mEventHandler = eventHandler;
    }

    public String getIpAddress() {
        return mSocket.getLocalAddress().getHostAddress();
    }
    
    public void setDebugMode(boolean state) {
        mDebug = state;
    }

    /**
     * writes a command to the server
     * 
     * @param command
     */
    private void write(String command) {
        String rawFtpCommnad = String.format(CMD_FORMAT, command);
        if (mDebug) {
            mEventHandler.onDebugMessage(rawFtpCommnad);
        }
        mWriter.print(rawFtpCommnad);
    }

    /**
     * reads the current stream
     */
    public void readStream() {
        try {

            FtpRawResponse response;

            while (mConnected) {
                response = parseStream(); // throws
                if (response == null) {
                    mEventHandler.onDisconnect();
                    return;
                }
                mEventHandler.onResponse(response.getCode(), response.getResponse());
            }

        } catch (IOException e) {
            e.printStackTrace(); // for now
        }
    }

    /**
     * Parses the stream
     * 
     * @return An FtpRawResponse
     * @throws IOException
     */
    public FtpRawResponse parseStream() throws IOException {

        String out = "";
        int i;
        char c;
        boolean finishRead = false;
        boolean lastLine = false;
        int counter = 0;
        int responseCode = -1;

        while (!finishRead) {
            i = mInputStream.read();

            if (i == -1) {
                return null;
            }

            c = (char) i;

            if (counter == 3 && responseCode == -1) {
                responseCode = Integer.parseInt(out);
            }

            if (counter == 3 && c != '-') {
                lastLine = true;
            }

            if (lastLine == true && i == '\n') {
                finishRead = true;
            }

            out += c;
            counter++;

            if (c == '\n') {
                counter = 0;
            }
        }

        return new FtpRawResponse(responseCode, out);

    }

    /** Below are command methods for communicating with the FTP server */

    public void newUser(String username) {
        write(String.format(USER, username));
    }

    public void password(String password) {
        write(String.format(PASS, password));
    }

    public void setAsciiMode() {
        write(String.format(TYPE, A));
    }

    public void setBinaryMode() {
        write(String.format(TYPE, I));
    }

    public void quit() {
        write(QUIT);
    }

    public void cdUp() {
        write(CDUP);
    }

    public void pwd() {
        write(PWD);
    }

    public void list() {
        write(LIST);
    }

    public void setPassiveMode() {
        write(PASSIVE);
    }

    public void setActiveMode(String formattedIp) {
        write(String.format(PORT, formattedIp));
    }

    public void cd(String dir) {
        write(String.format(CD, dir));
    }

    public void retrieve(String file) {
        write(String.format(RETRIEVE, file));
    }

}
