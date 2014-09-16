package com.floersch.brian.ftpChannels;

class FtpRawResponse {

    private int    mCode;
    private String mResponse;

    public FtpRawResponse(int code, String response) {
        mCode = code;
        mResponse = response;
    }

    public int getCode() {
        return mCode;
    }

    public String getResponse() {
        return mResponse;
    }
}