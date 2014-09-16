package com.floersch.brian.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FtpDataChannel implements Runnable {
	private Thread mThread;
	private Socket mSocket;
	private InputStream mInputStream;
	private PrintStream mWriter;
	private boolean mConnected;
	private IFtpDataChannelActions mEventHandler;

	public FtpDataChannel(String address, int port,
			IFtpDataChannelActions eventHandler) throws UnknownHostException,
			IOException {
		mSocket = new Socket(address, port);
		mWriter = new PrintStream(mSocket.getOutputStream());
		mInputStream = mSocket.getInputStream();
		mConnected = true;
		mEventHandler = eventHandler;
	}

	@Override
	public void run() {
		readStream();
	}

	public void start() {
		mThread = new Thread(this);
		mThread.start();
	}

	private void readStream() {
		int b;
		try {
			while ((b = mInputStream.read()) != -1) {
				mEventHandler.writeByte(b);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void join() throws InterruptedException {
		mThread.join();
	}
	
	
	public static IPAndPort decodePasv(String input) {
		
		Pattern p = Pattern.compile("\\((\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?),(\\d\\d?\\d?)\\)");
		Matcher m = p.matcher(input);
		
		if (m.find()) {
			String ip = "";
			int port = 0;
			for (int i = 1; i <= m.groupCount(); i++) {
				if (i < 4) {
					ip += m.group(i) + ".";
				} else if (i == 4){
					ip += m.group(i);
				} else if (i == 5) {
					port = Integer.parseInt(m.group(i));
					port *= 256;
				} else if (i == 6) {
					port += Integer.parseInt(m.group(i));
				}
			}
			return new IPAndPort(ip, port);
			
		}
		return null;
	}
	
}
