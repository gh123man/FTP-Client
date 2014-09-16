package com.floersch.brian.ftpClient;


class FtpClientState {

    private boolean              mPrintPayload = false;
    private boolean              mBlockInput   = false;
    private boolean              mPassiveMode  = false;
    private boolean              mInitialLogin = false;
    private IDataConnectCallback mCallback;

    public FtpClientState printPayload(boolean state) {
        mPrintPayload = state;
        return this;
    }

    public FtpClientState blockUserInput(boolean state) {
        mBlockInput = state;
        return this;
    }

    public FtpClientState passiveMode(boolean state) {
        mPassiveMode = state;
        return this;
    }

    public FtpClientState initialLoginComplete(boolean state) {
        mInitialLogin = state;
        return this;
    }

    public FtpClientState setOnDataConnectOneTimeCallback(IDataConnectCallback callback) {
        mCallback = callback;
        return this;
    }

    public boolean printPayload() {
        return mPrintPayload;
    }

    public boolean blockUserInput() {
        return mBlockInput;
    }

    public boolean passiveMode() {
        return mPassiveMode;
    }

    public boolean initialLoginComplete() {
        return mInitialLogin;
    }

    public void callOnDataConnect() {
        IDataConnectCallback callback = mCallback;
        mCallback = null;
        callback.onConnect();
    }

}
