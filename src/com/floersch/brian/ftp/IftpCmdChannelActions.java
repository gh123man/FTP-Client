package com.floersch.brian.ftp;

public interface IftpCmdChannelActions {

	void endOfResponse(int code, String response);
	
	void disconnected();
	
}
