


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.floersch.brian.ftpClient.FtpClient;
import com.floersch.brian.ftpClient.IFtpClientEvents;

/**
 * FTP command line interface
 * 
 * @author brian
 */
public class FTP implements IFtpClientEvents {

    /** Constants */
    public static final String NO_SERVER    = "Please specify a server to connect to.";
    public static final String BAD_PORT     = "Invalid port";
    public static final int    DEFAULT_PORT = 21;

    /** Members */
    private BufferedReader     mReader;

    /**
     * Main Method
     * 
     * @param args
     */
    public static void main(String[] args) {

        int port = DEFAULT_PORT;
        

        if (args == null) {
            System.out.println(NO_SERVER);
            return;
        }

        if (args.length == 2) {
            try {
                port = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.out.println(NO_SERVER);
                return;
            }

        }

        FTP ftpCommandLineInterface = new FTP();
        FtpClient client = new FtpClient(ftpCommandLineInterface);
        client.connect(args[0], port);

    }

    /**
     * Constructor
     */
    public FTP() {
        mReader = new BufferedReader(new InputStreamReader(System.in));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.floersch.brian.ftpClient.IFtpClientEvents#print(java.lang.String)
     */
    @Override
    public void print(String string) {
        System.out.print(string);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.floersch.brian.ftpClient.IFtpClientEvents#println(java.lang.String)
     */
    @Override
    public void println(String string) {
        System.out.println(string);

    }

    /*
     * (non-Javadoc)
     * 
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
