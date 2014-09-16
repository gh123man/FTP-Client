package com.floersch.brian.ftpChannels;

public class IpAndPort {
	
	private String mIp;
	private int mPort;
	
	public IpAndPort(String Ip, int port) {
		mIp = Ip;
		mPort = port;
	}

	public String getIp() {
		return mIp;
	}
	
	public int getPort() {
		return mPort;
	}
	
}