package com.floersch.brian.ftpCommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.floersch.brian.ftpClient.FtpClient;
import com.floersch.brian.ftpClient.IFtpClientEvents;

public class Ftp implements IFtpClientEvents {

    private BufferedReader mReader;

    public static void main(String[] args) {

        Ftp ftpCommandLineInterface = new Ftp();

        try {
            FtpClient client = new FtpClient("demo.wftpserver.com", 21, ftpCommandLineInterface);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Ftp() {
        mReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void print(String string) {
        System.out.print(string);

    }

    @Override
    public void println(String string) {
        System.out.println(string);

    }

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
