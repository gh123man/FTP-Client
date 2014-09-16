package com.floersch.brian.ftpClient;

/**
 * Class for managing an FTP client state
 * 
 * @author brian
 */
class FtpClientState {

    /** Members */
    private boolean              mPrintPayload = false;
    private boolean              mBlockInput   = false;
    private boolean              mPassiveMode  = false;
    private boolean              mInitialLogin = false;
    private IDataConnectCallback mCallback;

    /**
     * Constructor
     * 
     * @param state
     * @return
     */
    public FtpClientState setPrintPayload(boolean state) {
        mPrintPayload = state;
        return this;
    }

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

    public FtpClientState setOnDataConnectOneTimeCallback(IDataConnectCallback callback) {
        mCallback = callback;
        return this;
    }

    /** Getters */
    public boolean getPrintPayload() {
        return mPrintPayload;
    }

    public boolean getBlockUserInput() {
        return mBlockInput;
    }

    public boolean getPassiveMode() {
        return mPassiveMode;
    }

    public boolean getInitialLoginComplete() {
        return mInitialLogin;
    }

    /**
     * Calls the callback.
     */
    public void callOnDataConnect() {
        IDataConnectCallback callback = mCallback;
        mCallback = null;
        callback.onConnect();
    }

}
