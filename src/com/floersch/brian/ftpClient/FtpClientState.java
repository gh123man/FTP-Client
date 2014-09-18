package com.floersch.brian.ftpClient;


/**
 * Class for managing an FTP client state
 * 
 * @author brian
 */
class FtpClientState {

    /** Members */
    private boolean            mBlockInput   = false;
    private boolean            mPassiveMode  = false;
    private boolean            mInitialLogin = false;
    private IDataChannelClientEvents mCallback;

    /**
     * Constructor
     * 
     * @param state
     * @return
     */

    /** Setters */
    public FtpClientState setBlockUserInput(boolean state) {
        mBlockInput = state;
        return this;
    }

    public FtpClientState setPassiveMode(boolean state) {
        mPassiveMode = state;
        return this;
    }

    public FtpClientState setInitialLoginComplete(boolean state) {
        mInitialLogin = state;
        return this;
    }

    public FtpClientState setDataChannelClientEventListener(IDataChannelClientEvents callback) {
        mCallback = callback;
        return this;
    }

    /** Getters */
    public boolean getBlockUserInput() {
        return mBlockInput;
    }

    public boolean getPassiveMode() {
        return mPassiveMode;
    }

    public boolean getInitialLoginComplete() {
        return mInitialLogin;
    }

    public IDataChannelClientEvents getDataChannelClientEventListener() {
        return mCallback;
    }

}
