package com.floersch.brian.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class FtpCmdChannel implements Runnable {
	
	/** response codes */
	public static final int READY_FOR_NEW_USER         = 220;
	public static final int NOT_LOGGED_IN              = 530;
	public static final int UNAME_OK_NEED_PASS         = 331;
	public static final int USER_LOGGED_IN             = 230;
	public static final int ENTERING_PASSIVE_MODE      = 227;
	public static final int CLOSING_DATA_CHANNEL       = 226;
	public static final int ABOUT_TO_OPEN_DATA_CHANNEL = 150;
	public static final int CANNOT_OPEN_DATA_CHANNEL   = 425;
	
	/** commands */
	public static final String USER        = "USER %s";
	public static final String PASS    	   = "PASS %s";
	public static final String PASSIVE     = "PASV";
	public static final String LIST        = "LIST";
	public static final String TYPE_ASCII  = "TYPE A";
	public static final String TYPE_BINARY = "TYPE I";
	public static final String CDUP        = "CDUP";
	public static final String QUIT        = "QUIT";
	public static final String PWD         = "PWD";
	

	private Thread mThread;
	private Socket mSocket;
	private InputStream mInputStream;
	private PrintStream mWriter;
	private boolean mConnected;
	private IftpCmdChannelActions mEventHandler;
	
	
	public FtpCmdChannel(String address, int port, IftpCmdChannelActions eventHandler) throws UnknownHostException, IOException {
		mSocket = new Socket(address, port);
		mWriter = new PrintStream(mSocket.getOutputStream());
		mInputStream = mSocket.getInputStream();
		mConnected = true;
		mEventHandler = eventHandler;
	}
	
	@Override
	public void run() {
		readServer();
		
	}
	
	public void start() {
		mThread = new Thread(this);
		mThread.start();
	}
	
	
	public void write(String command) {
		mWriter.println(command);
	}
	
	
	private void readServer() {
		try {
			
			FtpRawResponse response;
			
			while(mConnected) {
				response = getBuffer(); //throws
				if (response == null) {
					mEventHandler.disconnected();
					return;
				}
				mEventHandler.endOfResponse(response.getCode(), response.getResponse());
			}
			
		} catch (IOException e) {
			e.printStackTrace(); //for now
		}
	}
	
	
	
	private FtpRawResponse getBuffer() throws IOException {
		
		String out = "";
		int i;
		char c;
		boolean finishRead = false;
		boolean lastLine = false;
		int counter = 0;
		int responseCode = -1;
		
		
		while(!finishRead) {
			i = mInputStream.read();
			
			if (i == -1) {
				return null;
			}
			
			c = (char)i;
			
			if (counter == 3 && responseCode == -1) {
				responseCode = Integer.parseInt(out);
			}
			
			if (counter == 3 && c != '-') {
				lastLine = true;
			}
			
			if (lastLine == true && i == '\n') {
				finishRead = true;
			}
			
			out += c;
			counter++;
			
			if (c == '\n') {
				counter = 0;
			}
		}
		
		return new FtpRawResponse(responseCode, out);
		
	}
	
	private class FtpRawResponse {
		
		private int mCode;
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
	
	
}
