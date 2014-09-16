package com.floersch.brian.ftpCommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.floersch.brian.ftpClient.FtpClient;
import com.floersch.brian.ftpClient.IFtpClientEvents;

/**
 * FTP command line interface
 * @author brian
 */
public class Ftp implements IFtpClientEvents {

    /** Members */
    private BufferedReader mReader;

    /**
     * Main Method
     * @param args
     */
    public static void main(String[] args) {

        Ftp ftpCommandLineInterface = new Ftp();

        try {
            new FtpClient("demo.wftpserver.com", 21, ftpCommandLineInterface);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Constructor
     */
    public Ftp() {
        mReader = new BufferedReader(new InputStreamReader(System.in));
    }

    /*
     * (non-Javadoc)
     * @see com.floersch.brian.ftpClient.IFtpClientEvents#print(java.lang.String)
     */
    @Override
    public void print(String string) {
        System.out.print(string);

    }

    /*
     * (non-Javadoc)
     * @see com.floersch.brian.ftpClient.IFtpClientEvents#println(java.lang.String)
     */
    @Override
    public void println(String string) {
        System.out.println(string);

    }

    /*
     * (non-Javadoc)
     * @see com.floersch.brian.ftpClient.IFtpClientEvents#requestInput()
     */
    @Override
    public String requestInput() {
        try {
            return mReader.readLine();
        } catch (IOException e) {
            e.printStackTrace(); // TODO
        }
        return null;
    }
}
