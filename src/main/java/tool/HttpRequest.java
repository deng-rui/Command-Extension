package extension.tool;

import arc.Core;
import arc.files.Fi;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpRequest {

	private static final String USER_AGENT = "Mozilla/5.0";

	public static String doGet(String url) throws Exception {
		URL conn = new URL(url);
		HttpURLConnection con = (HttpURLConnection) conn.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}


	public static void Url302(String url,String name,Fi filePath){
		try {
			URL serverUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setRequestMethod("GET");
			// 必须设置false，否则会自动redirect到Location的地址
			conn.setInstanceFollowRedirects(false);
			conn.addRequestProperty("Accept-Charset", "UTF-8;");
			conn.addRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
			conn.connect();
			String location = conn.getHeaderField("Location");
			downUrl(location,name,filePath);
 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void downUrl(String url,String name,Fi filePath){
		File file=filePath.file();
		 //判断文件夹是否存在
		if (!file.exists())file.mkdirs();
		FileOutputStream fileOut = null;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try{
			// 建立链接
			URL httpUrl=new URL(url);
			conn=(HttpURLConnection) httpUrl.openConnection();
			//以Post方式提交表单，默认get方式
			conn.setDoInput(true);  
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.connect();
			inputStream=conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			//写入到文件
			fileOut = new FileOutputStream(filePath+File.separator+name);
			BufferedOutputStream bos = new BufferedOutputStream(fileOut);
			byte[] buf = new byte[4096];
			int length = bis.read(buf);
			while(length != -1){
				bos.write(buf, 0, length);
				length = bis.read(buf);
			}
			bos.close();
			bis.close();
			conn.disconnect();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		 
	}

}