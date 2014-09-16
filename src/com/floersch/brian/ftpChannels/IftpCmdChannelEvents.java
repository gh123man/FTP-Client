package com.floersch.brian.ftpChannels;

public interface IftpCmdChannelEvents {

	void endOfResponse(int code, String response);
	
	void disconnected();
	
}
