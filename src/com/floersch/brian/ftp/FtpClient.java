package com.floersch.brian.ftp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class FtpClient implements IftpCmdChannelActions, IFtpDataChannelActions {
	
	/** Messages */
	private static final String NAME              = "Name:";
	private static final String PASSWORD          = "Password:";
	private static final String PROMPT            = "ftp>:";
	private static final String DISCONNECTED      = "\nDISCONNECTED!!!!";
	private static final String AMBIGUOUS_COMMAND = "?Ambiguous command";
	
	/** States */
	private static final int MODE_NONE    = 0;
	private static final int MODE_PASSIVE = 1;
	private static final int MODE_ACTIVE  = 2;
	
	/** user commands */
	private static final String DIR     = "dir";
	private static final String ASCII   = "ascii";
	private static final String BINARY  = "binary";
	private static final String QUIT    = "quit";
	private static final String CDUP    = "cdup";
	private static final String PWD     = "pwd";
	private static final String PASSIVE = "passive";
	
	
	/** Members */
	private FtpCmdChannel mServer;
	private BufferedReader mReader = new BufferedReader(new InputStreamReader(System.in));
	private int mMode = MODE_NONE;
	private FtpDataChannel mDataChannel;
	private boolean mPrintPayload = false;
	private boolean mBlockInput = false;
	private ByteArrayOutputStream mDataBuffer;
	
	

	public FtpClient(String address, int port) throws UnknownHostException,
			IOException {
		mServer = new FtpCmdChannel(address, port, this);
		mServer.start();
	}
	
	@Override
	public void writeByte(int b) {
		mDataBuffer.write(b);
	}


	@Override
	public void disconnected() {
		System.out.println(DISCONNECTED);
	}

	@Override
	public void endOfResponse(int code, String response) {
		
		boolean responseHandled = false;
		
		switch (code) {
		
			case FtpCmdChannel.READY_FOR_NEW_USER:
			case FtpCmdChannel.NOT_LOGGED_IN:
				
				System.out.print(response);
				System.out.print(NAME);
				mServer.write(FtpCmdChannel.USER + " " + getUserInput());
				responseHandled = true;
				break;
				
			
			case FtpCmdChannel.UNAME_OK_NEED_PASS:
				
				System.out.print(response);
				System.out.print(PASSWORD);
				mServer.write(FtpCmdChannel.PASS + " " + getUserInput());
				responseHandled = true;
				break;
				
			case FtpCmdChannel.USER_LOGGED_IN:
				break;
				
			case FtpCmdChannel.ENTERING_PASSIVE_MODE:
				
				openDataChannel(FtpDataChannel.decodePasv(response));
				break;
				
			case FtpCmdChannel.ABOUT_TO_OPEN_DATA_CHANNEL:
				break;
				
			case FtpCmdChannel.CLOSING_DATA_CHANNEL:
				
				waitForDataChannel();
				handlePayload();
				mBlockInput = false;
				break;

			default:
				
				break;
		}
		
		if (!responseHandled) {
			System.out.print(response);
			if (!mBlockInput) {
				prompt();
			}
			
		}

	}
	
	private void prompt() {
		System.out.print(PROMPT);
		parseCommands(getUserInput());
	}
	
	private void parseCommands(String input) {
		switch (input) {
			case DIR:
				mPrintPayload = true;
				mBlockInput = true;
				mServer.write(FtpCmdChannel.PASSIVE); //active by default?
				mServer.write(FtpCmdChannel.LIST);
				break;
				
			case ASCII:
				mServer.write(FtpCmdChannel.TYPE_ASCII);
				break;
				
			case BINARY:
				mServer.write(FtpCmdChannel.TYPE_BINARY);
				break;
				
			case QUIT:
				mBlockInput = true;
				mServer.write(FtpCmdChannel.QUIT);
				break;
				
			case CDUP:
				mServer.write(FtpCmdChannel.CDUP);
				
			case PWD:
				mServer.write(FtpCmdChannel.PWD);
				
				
			default:
				if (!input.equals("")) {
					System.out.println(AMBIGUOUS_COMMAND);
				}
				prompt();
				break;
		}
		
	}
	
	
	private String getUserInput() {
		try {
			return mReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); //fix this
		}
		return null;
	}
	
	private void waitForDataChannel() {
		try {
			mDataChannel.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void openDataChannel(IPAndPort ipAndPort) {
		try {
			mDataChannel = new FtpDataChannel(ipAndPort.getIp(), ipAndPort.getPort(), this);
			mDataBuffer = new ByteArrayOutputStream();
			mDataChannel.start();
		} catch (IOException e) {
			e.printStackTrace(); //fix
		}
	}
	
	private void handlePayload() {
		if (mPrintPayload){
			System.out.print(mDataBuffer.toString());
		}
	}
	


}
