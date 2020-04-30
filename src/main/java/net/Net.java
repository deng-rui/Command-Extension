package extension.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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