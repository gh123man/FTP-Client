package com.floersch.brian.ftpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import com.floersch.brian.ftpChannels.ActiveDataChannel;
import com.floersch.brian.ftpChannels.FtpCmdChannel;
import com.floersch.brian.ftpChannels.FtpDataChannel;
import com.floersch.brian.ftpChannels.IFtpDataChannelEvents;
import com.floersch.brian.ftpChannels.IftpCmdChannelEvents;
import com.floersch.brian.ftpChannels.IpAndPort;
import com.floersch.brian.ftpChannels.PassiveDataChannel;

/**
 * Manages connections to an FTP server
 * 
 * @author brian
 */
public class FtpClient implements IftpCmdChannelEvents {

    /** Messages */
    private static final String NAME              = "Name:";
    private static final String PASSWORD          = "Password:";
    private static final String PROMPT            = "ftp>:";
    private static final String DISCONNECTED      = "\nConnection closed by foreign host.";
    private static final String AMBIGUOUS_COMMAND = "?Ambiguous command";
    private static final String PASSIVE_MODE      = "Passive mode %s";
    private static final String ON                = "on";
    private static final String OFF               = "off";

    /** user commands */
    private static final String DIR               = "dir";
    private static final String ASCII             = "ascii";
    private static final String BINARY            = "binary";
    private static final String QUIT              = "quit";
    private static final String CDUP              = "cdup";
    private static final String PWD               = "pwd";
    private static final String PASSIVE           = "passive";
    private static final String USER              = "user";
    private static final String CD                = "cd";

    /** other constants */
    private static final String FIND_CMD          = "(.*?)\\s";

    /** Members */
    private FtpClientState      mState;
    private IFtpClientEvents    mEventHandler;
    private FtpCmdChannel       mCmdChannel;
    private FtpDataChannel      mDataChannel;

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
        mEventHandler = eventHandler;

            mCmdChannel.readStream();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.floersch.brian.ftpChannels.IftpCmdChannelEvents#disconnected()
     */
    @Override
    public void disconnected() {
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
    public void endOfResponse(int code, String response) {

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
                openPassiveDataChannel(PassiveDataChannel.decodePasv(response), mState.getDataChannelEventListener());
                mDataChannel.initlize();
                break;
                
            case FtpCmdChannel.PORT_SUCCESS:
                mDataChannel.initlize();
                break;

            case FtpCmdChannel.CANNOT_OPEN_DATA_CHANNEL:
                break;

            case FtpCmdChannel.ABOUT_TO_OPEN_DATA_CHANNEL:
                mEventHandler.print(response);
                mDataChannel.onChannelOpen();
                responseHandled = true;
                break;

            case FtpCmdChannel.CLOSING_DATA_CHANNEL:
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

                mState.setBlockUserInput(true);

                // set up onConnect callback
                mState.setOnDataChannelEventListener(new IFtpDataChannelEvents() {
                    @Override
                    public void onConnect(FtpDataChannel dataChannel) {
                        mCmdChannel.list();
                    }

                    @Override
                    public void onChannelOpen(FtpDataChannel dataChannel) {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        dataChannel.copyStream(outStream);
                        mEventHandler.print(outStream.toString());
                        
                    }
                });

                setDataChannelMode();
                

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
     * Opens a data channel
     * 
     * @param ipAndPort
     */
    private void openPassiveDataChannel(IpAndPort ipAndPort, IFtpDataChannelEvents eventListner) {
        try {
            mDataChannel = new PassiveDataChannel(ipAndPort, eventListner);
        } catch (IOException e) {
            e.printStackTrace(); // TODO
        }
    }

    private void openActiveDataChannel(IFtpDataChannelEvents eventListner) {
        try {
            mDataChannel = new ActiveDataChannel(eventListner);
            mCmdChannel.setActiveMode(ActiveDataChannel.encodeIpAndPort(((ActiveDataChannel)mDataChannel).getIpAndFreePort()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
    /**
     * Sets the data channel mode on the server (not local)
     */
    private void setDataChannelMode() {
        if (mState.getPassiveMode()) {
            mCmdChannel.setPassiveMode();
        } else {
            openActiveDataChannel(mState.getDataChannelEventListener());
        }
    }

}
