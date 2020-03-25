package extension.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//Java

import extension.util.Log;
//GA-Exted

public class Net {

	public static boolean isConnect() {
		Runtime runtime = Runtime.getRuntime();
		Process process;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			String cmd = "ping z.cn -c 2";
			String os = System.getProperty("os.name");
			if(os.toLowerCase().startsWith("win")) cmd = "ping z.cn -n 2";
			process = runtime.exec(cmd);
			is = process.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String line = null;
			StringBuffer result_ttl = new StringBuffer();
			while ((line = br.readLine()) != null) {
				result_ttl.append(line);
			}
			if (null != result_ttl && !result_ttl.toString().equals("")) {
				if(os.toLowerCase().startsWith("win")) if (result_ttl.toString().indexOf("TTL") > 0) return true;
				if (result_ttl.toString().indexOf("ttl") > 0) return true;			
			}
			is.close();
			isr.close();
			br.close();
		} catch (IOException e) {
			Log.error("Net-Connect",e);
		}
		return false;
	}
}