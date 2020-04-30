package extension.net;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//Java

import extension.util.log.Exceptions;
import extension.util.log.Log;
//GA-Exted

public class Net {
	public static boolean isConnect() {
		boolean connect = false;  
		InputStream in = null; 
		try {
			URL url = new URL("https://baidu.com");  
			try {  
				in = url.openStream();   
				connect = true;
			} catch (IOException e) {
				
			} finally {
				try {
					if(in != null) {
                        in.close();
                    }
				} catch (IOException e) {
					in = null;
				}
			}
		} catch (MalformedURLException e) {  
		}
		return connect;
	}
}