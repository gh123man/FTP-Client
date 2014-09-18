package com.floersch.brian.ftpChannels;

/**
 * A class representing a response code and its message body
 * 
 * @author Brian Floersch (bpf4935@rit.edu)
 */
public class FtpRawResponse {

    /** Members */
    private int    mCode;
    private String mResponse;

    /**
     * Constructs a new ftp response
     * 
     * @param code
     * @param response
     */
    protected FtpRawResponse(int code, String response) {
        mCode = code;
        mResponse = response;
    }

    /**
     * returns the response code
     * 
     * @return
     */
    public int getCode() {
        return mCode;
    }

    /**
     * returns the response body
     * 
     * @return
     */
    public String getResponse() {
        return mResponse;
    }
}