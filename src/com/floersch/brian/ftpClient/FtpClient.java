package com.floersch.brian.ftpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import com.floersch.brian.ftpChannels.FtpCmdChannel;
import com.floersch.brian.ftpChannels.FtpDataChannel;
import com.floersch.brian.ftpChannels.IFtpDataChannelEvents;
import com.floersch.brian.ftpChannels.IftpCmdChannelEvents;
import com.floersch.brian.ftpChannels.IpAndPort;

/**
 * Manages connections to an FTP server
 * 
 * @author brian
 */
public class FtpClient implements IftpCmdChannelEvents, IFtpDataChannelEvents {

    /** Messages */
    private static final String   NAME              = "Name:";
    private static final String   PASSWORD          = "Password:";
    private static final String   PROMPT            = "ftp>:";
    private static final String   DISCONNECTED      = "\nConnection closed by foreign host.";
    private static final String   AMBIGUOUS_COMMAND = "?Ambiguous command";
    private static final String   PASSIVE_MODE      = "Passive mode %s";
    private static final String   ON                = "on";
    private static final String   OFF               = "off";

    /** user commands */
    private static final String   DIR               = "dir";
    private static final String   ASCII             = "ascii";
    private static final String   BINARY            = "binary";
    private static final String   QUIT              = "quit";
    private static final String   CDUP              = "cdup";
    private static final String   PWD               = "pwd";
    private static final String   PASSIVE           = "passive";
    private static final String   USER              = "user";
    private static final String   CD                = "cd";

    /** other constants */
    private static final String   FIND_CMD          = "(.*?)\\s";

    /** Members */
    private FtpClientState        mState;
    private IFtpClientEvents      mEventHandler;
    private FtpCmdChannel         mCmdChannel;
    private FtpDataChannel        mDataChannel;
    private ByteArrayOutputStream mDataBuffer;

    /**
     * Constructor
     * 
     * @param address
     * @param port
     * @param eventHandler
     * @throws UnknownHostException
     * @throws IOException
     */
    public FtpClient(String address, int port, IFtpClientEvents eventHandler) throws UnknownHostException, IOException {
        mState = new FtpClientState();
        mCmdChannel = new FtpCmdChannel(address, port, this);
        mCmdChannel.start();
        mEventHandler = eventHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.floersch.brian.ftpChannels.IFtpDataChannelEvents#onConnect()
     */
    @Override
    public synchronized void onConnect() {
        mState.callOnDataConnect();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.floersch.brian.ftpChannels.IFtpDataChannelEvents#writeByte(int)
     */
    @Override
    public synchronized void writeByte(int b) {
        mDataBuffer.write(b);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.floersch.brian.ftpChannels.IftpCmdChannelEvents#disconnected()
     */
    @Override
    public synchronized void disconnected() {
        mEventHandler.println(DISCONNECTED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.floersch.brian.ftpChannels.IftpCmdChannelEvents#endOfResponse(int,
     * java.lang.String)
     */
    @Override
    public synchronized void endOfResponse(int code, String response) {

        boolean responseHandled = false;

        switch (code) {

            case FtpCmdChannel.NOT_LOGGED_IN:
                if (mState.getInitialLoginComplete()) {
                    break;
                }

            case FtpCmdChannel.READY_FOR_NEW_USER:
                mEventHandler.print(response);
                mEventHandler.print(NAME);
                mCmdChannel.newUser(mEventHandler.requestInput());
                responseHandled = true;
                break;

            case FtpCmdChannel.UNAME_OK_NEED_PASS:

                mState.setInitialLoginComplete(true);
                mEventHandler.print(response);
                mEventHandler.print(PASSWORD);
                mCmdChannel.password(mEventHandler.requestInput());
                responseHandled = true;
                break;

            case FtpCmdChannel.USER_LOGGED_IN:
                break;

            case FtpCmdChannel.ENTERING_PASSIVE_MODE:

                openDataChannel(FtpDataChannel.decodePasv(response));
                break;

            case FtpCmdChannel.CANNOT_OPEN_DATA_CHANNEL:
                break;

            case FtpCmdChannel.ABOUT_TO_OPEN_DATA_CHANNEL:
                break;

            case FtpCmdChannel.CLOSING_DATA_CHANNEL:

                waitForDataChannel();

                if (mState.getPrintPayload()) {
                    mEventHandler.print(mDataBuffer.toString());
                }

                mState.setBlockUserInput(false);
                break;

            case FtpCmdChannel.PORT_SUCCESS:
                break;

            default:
                break;
        }

        if (!responseHandled) {
            mEventHandler.print(response);
            if (!mState.getBlockUserInput()) {
                prompt();
            }

        }

    }

    /**
     * Prompts the event handler for data
     */
    private void prompt() {
        mEventHandler.print(PROMPT);
        parseCommands(mEventHandler.requestInput());
    }

    /**
     * Parses the passed commands
     * 
     * @param input
     */
    private void parseCommands(String input) {

        String command = null;
        String args = null;
        if (input.contains(" ")) {
            command = input.split(" ")[0];
            args = input.split(FIND_CMD)[1];
        } else {
            command = input;
        }

        switch (command) {
            case DIR:

                mState.setPrintPayload(true).setBlockUserInput(true);
                setDataChannelMode();

                // set up onConnect callback
                mState.setOnDataConnectOneTimeCallback(new IDataConnectCallback() {
                    @Override
                    public void onConnect() {
                        mCmdChannel.list();
                    }
                });
                break;

            case ASCII:
                mCmdChannel.setAsciiMode();
                break;

            case BINARY:
                mCmdChannel.setBinaryMode();
                break;

            case QUIT:
                mState.setBlockUserInput(true);
                mCmdChannel.quit();
                break;

            case CDUP:
                mCmdChannel.cdUp();
                break;

            case PWD:
                mCmdChannel.pwd();
                break;

            case PASSIVE:
                if (mState.setPassiveMode(!mState.getPassiveMode()).getPassiveMode()) {
                    mEventHandler.println(String.format(PASSIVE_MODE, ON));
                } else {
                    mEventHandler.println(String.format(PASSIVE_MODE, OFF));
                }
                prompt();
                break;

            case USER:
                mCmdChannel.newUser(args);
                break;

            case CD:
                mCmdChannel.cd(args);
                break;

            default:
                if (!input.equals("")) {
                    mEventHandler.println(AMBIGUOUS_COMMAND);
                }
                prompt();
                break;
        }
    }

    /**
     * Waits for the data channel thread to finish
     */
    private void waitForDataChannel() {
        try {
            mDataChannel.join();
        } catch (InterruptedException e) {
            e.printStackTrace(); // TODO
        }
    }

    /**
     * Opens a data channel
     * 
     * @param ipAndPort
     */
    private void openDataChannel(IpAndPort ipAndPort) {
        try {
            mDataChannel = new FtpDataChannel(ipAndPort.getIp(), ipAndPort.getPort(), this);
            mDataBuffer = new ByteArrayOutputStream();
            mDataChannel.start();
        } catch (IOException e) {
            e.printStackTrace(); // TODO
        }
    }

    /**
     * Sets the data channel mode on the server (not local)
     */
    private void setDataChannelMode() {
        if (mState.getPassiveMode()) {
            mCmdChannel.setPassiveMode();
        } else {
            // active mode
        }
    }

}
