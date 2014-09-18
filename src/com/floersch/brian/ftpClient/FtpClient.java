package com.floersch.brian.ftpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import com.floersch.brian.ftpChannels.ActiveDataChannel;
import com.floersch.brian.ftpChannels.CommandChannel;
import com.floersch.brian.ftpChannels.DataChannel;
import com.floersch.brian.ftpChannels.ICommandChannelEvents;
import com.floersch.brian.ftpChannels.IpAndPort;
import com.floersch.brian.ftpChannels.PassiveDataChannel;

/**
 * Manages connections to an FTP server
 * 
 * @author brian
 */
public class FtpClient implements ICommandChannelEvents {

    /** Messages */
    private static final String TRYING            = "Trying %s...";
    private static final String CONNECTED         = "Connected to %s";
    private static final String FAIL_TO_CONNECT   = "Failed to connect to sever";
    private static final String NAME              = "Name:";
    private static final String PASSWORD          = "Password:";
    private static final String PROMPT            = "ftp>:";
    private static final String DISCONNECTED      = "\nConnection closed by foreign host.";
    private static final String AMBIGUOUS_COMMAND = "?Ambiguous command";
    private static final String PASSIVE_MODE      = "Passive mode %s";
    private static final String DEBUG_MODE        = "Debug mode %s";
    private static final String ON                = "on";
    private static final String OFF               = "off";

    /** user commands */
    private static final String DIR               = "dir";
    private static final String LS                = "ls";
    private static final String ASCII             = "ascii";
    private static final String BINARY            = "binary";
    private static final String QUIT              = "quit";
    private static final String EXIT              = "exit";
    private static final String CDUP              = "cdup";
    private static final String PWD               = "pwd";
    private static final String PASSIVE           = "passive";
    private static final String USER              = "user";
    private static final String CD                = "cd";
    private static final String GET               = "get";
    private static final String DEBUG             = "debug";

    /** other constants */
    private static final String FIND_CMD          = "(.*?)\\s";
    private static final String PATH_FORMAT       = "%s/%s";
    private static final String DEBUG_FORMAT      = "---> %s";

    /** Members */
    private FtpClientState      mState;
    private IFtpClientEvents    mEventHandler;
    private CommandChannel      mCmdChannel;
    private DataChannel         mDataChannel;

    /**
     * Constructor
     * 
     * @param address
     * @param port
     * @param eventHandler
     * @throws UnknownHostException
     * @throws IOException
     */
    public FtpClient(IFtpClientEvents eventHandler) {
        mEventHandler = eventHandler;
        mState = new FtpClientState();
    }

    public void connect(String address, int port) {
        mEventHandler.println(String.format(TRYING, address));

        try {
            mCmdChannel = new CommandChannel(address, port, this);
        } catch (UnknownHostException e) {
            mEventHandler.println(FAIL_TO_CONNECT);
        } catch (IOException e) {
            mEventHandler.println(FAIL_TO_CONNECT);
        }

        if (mCmdChannel != null) {
            mEventHandler.println(String.format(CONNECTED, mCmdChannel.getIpAddress()));
            mCmdChannel.readStream();
        }
    }

    @Override
    public void onDisconnect() {
        mEventHandler.println(DISCONNECTED);
    }

    @Override
    public void onDebugMessage(String message) {
        mEventHandler.print(String.format(DEBUG_FORMAT, message));
    }

    @Override
    public void onResponse(int code, String response) {

        boolean responseHandled = false;

        switch (code) {

            case CommandChannel.NOT_LOGGED_IN:
                if (mState.getInitialLoginComplete()) {
                    break;
                }

            case CommandChannel.READY_FOR_NEW_USER:
                mEventHandler.print(response);
                mEventHandler.print(NAME);
                mCmdChannel.newUser(mEventHandler.requestInput());
                responseHandled = true;
                break;

            case CommandChannel.UNAME_OK_NEED_PASS:

                mState.setInitialLoginComplete(true);
                mEventHandler.print(response);
                mEventHandler.print(PASSWORD);
                mCmdChannel.password(mEventHandler.requestInput());
                responseHandled = true;
                break;

            case CommandChannel.USER_LOGGED_IN:
                break;

            case CommandChannel.ENTERING_PASSIVE_MODE:
                openPassiveDataChannel(PassiveDataChannel.decodePasv(response));
                if (mState.getDataChannelClientEventListener() != null) {
                    mEventHandler.print(response);
                    mState.getDataChannelClientEventListener().onReady();
                    responseHandled = true;
                }
                break;

            case CommandChannel.SUCCESS:
                if (mState.getDataChannelClientEventListener() != null) {
                    mEventHandler.print(response);
                    mState.getDataChannelClientEventListener().onReady();
                    responseHandled = true;
                }
                break;

            case CommandChannel.CANNOT_OPEN_DATA_CHANNEL:
                break;

            case CommandChannel.ABOUT_TO_OPEN_DATA_CHANNEL:
                mEventHandler.print(response);
                mDataChannel.openStream();
                mState.getDataChannelClientEventListener().onChannelOpen();
                mState.setDataChannelClientEventListener(null);
                responseHandled = true;
                break;

            case CommandChannel.CLOSING_DATA_CHANNEL:
                mState.setBlockUserInput(false);
                break;

            case CommandChannel.FILE_NOT_FOUND:
                mState.setBlockUserInput(false);
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
            case LS:
            case DIR:
                mState.setBlockUserInput(true);

                mState.setDataChannelClientEventListener(new IDataChannelClientEvents() {
                    @Override
                    public void onReady() {
                        mCmdChannel.list();
                    }

                    @Override
                    public void onChannelOpen() {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        mDataChannel.copyStream(outStream);
                        mEventHandler.print(outStream.toString());
                    }
                });
                requestOpenDataChannel();
                break;

            case GET:
                mState.setBlockUserInput(true);

                final String fileName = args;

                mState.setDataChannelClientEventListener(new IDataChannelClientEvents() {
                    @Override
                    public void onReady() {
                        mCmdChannel.retrieve(fileName);
                    }

                    @Override
                    public void onChannelOpen() {
                        try {
                            File file = new File(String.format(PATH_FORMAT, System.getProperty("user.dir"), fileName));
                            FileOutputStream outStream = new FileOutputStream(file);
                            mDataChannel.copyStream(outStream);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace(); // TODO
                        }
                    }
                });
                requestOpenDataChannel();
                break;
            case ASCII:
                mCmdChannel.setAsciiMode();
                break;

            case BINARY:
                mCmdChannel.setBinaryMode();
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

            case DEBUG:
                if (mState.setDebugMode(!mState.getDebugMode()).getDebugMode()) {
                    mEventHandler.println(String.format(DEBUG_MODE, ON));
                    mCmdChannel.setDebugMode(true);
                } else {
                    mEventHandler.println(String.format(DEBUG_MODE, OFF));
                    mCmdChannel.setDebugMode(false);
                }
                prompt();
                break;

            case EXIT:
            case QUIT:
                mState.setBlockUserInput(true);
                mCmdChannel.quit();
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
     * Prompts the event handler for data
     */
    private void prompt() {
        mEventHandler.print(PROMPT);
        parseCommands(mEventHandler.requestInput());
    }

    /**
     * Opens a data channel
     * 
     * @param ipAndPort
     */
    private void openPassiveDataChannel(IpAndPort ipAndPort) {
        try {
            mDataChannel = new PassiveDataChannel(ipAndPort);
        } catch (IOException e) {
            e.printStackTrace(); // TODO
        }
    }

    /**
     * Sets the data channel mode on the server (not local)
     */
    private void requestOpenDataChannel() {
        if (mState.getPassiveMode()) {
            mCmdChannel.setPassiveMode();
        } else {
            startActiveDataChannel();
        }
    }

    private void startActiveDataChannel() {
        try {
            mDataChannel = new ActiveDataChannel();
            mCmdChannel.setActiveMode(ActiveDataChannel.encodeIpAndPort(((ActiveDataChannel) mDataChannel).getIpAndFreePort()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
