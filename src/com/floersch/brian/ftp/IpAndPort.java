package com.floersch.brian.ftp;

class IPAndPort {
	
	private String mIp;
	private int mPort;
	
	public IPAndPort(String Ip, int port) {
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