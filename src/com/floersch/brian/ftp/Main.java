package com.floersch.brian.ftp;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		try {
			FtpClient client = new FtpClient("ftp.mozilla.org", 21);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
