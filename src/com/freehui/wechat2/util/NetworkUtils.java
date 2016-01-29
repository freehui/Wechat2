package com.freehui.wechat2.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class NetworkUtils {

	public static byte[] readFromSocket(Socket s, int length) {
		
		byte[] data = new byte[length];
		InputStream in;
		int len = 0;
		int start = 0;
		try {
			in = s.getInputStream();
			while ((len = in.read(data, start, length - len)) > 0) {

				start += len;
				if (start == length)
					break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}

		return data;
	}
}
