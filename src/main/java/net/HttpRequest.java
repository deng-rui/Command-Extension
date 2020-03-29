package extension.net;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import extension.data.global.Config;
import extension.util.file.FileUtil;
import extension.util.Log;
import extension.util.log.Exceptions;
//GA-Exted

import static extension.util.IsBlankUtil.Blank;

public class HttpRequest {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36";
	private static final String USER_TESTS = "Mozilla/5.0 (JAVA 11; x64) HI JAVA TO WEB";

	public static String doGet(String url) throws Exception {
		return doGet(url,null,null);
	}

	public static String doGet(String url, String ref, String cookie) throws Exception {
		HttpURLConnection con = null;
		BufferedReader in = null;
		StringBuffer response = new StringBuffer();
		try {
			URL conn = new URL(url);
			con = (HttpURLConnection) conn.openConnection();
			con.setRequestMethod("GET");
			con.addRequestProperty("Accept-Charset", "UTF-8");
			if(Blank(ref))con.setRequestProperty("Referrer", ref);
			if(Blank(cookie))con.setRequestProperty("Cookie", cookie);
			con.setRequestProperty("User-Agent", USER_AGENT);
			int responseCode = con.getResponseCode();
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} finally{
			if(in != null) in.close();
			if(con != null) con.disconnect();
		}
		return response.toString();
	}

	public static String doPost(String url, String param) throws Exception {
		return doPost(url,param,null,null);
	}

	public static String doPost(String url, String param, String ref, String cookie) throws Exception {
		String result = "";
		URL realUrl = new URL(url);
		//打开和URL之间的连接
		URLConnection conn =  realUrl.openConnection();
		//设置通用的请求属性
		conn.setRequestProperty("accept", "*/*");
		conn.setRequestProperty("connection", "Keep-Alive");
		if(Blank(ref))conn.setRequestProperty("Referrer", ref);
		if(Blank(cookie))conn.setRequestProperty("Cookie", cookie);
		conn.setRequestProperty("User-Agent",USER_AGENT);
		//发送POST请求必须设置如下两行
		conn.setDoOutput(true);
		conn.setDoInput(true);
		//获取URLConnection对象对应的输出流
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		//发送请求参数
		out.print(param);
		//flush输出流的缓冲
		out.flush();
		// 定义 BufferedReader输入流来读取URL的响应
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
		String line;
		while ((line = in.readLine()) != null) {
			result += "\n" + line;
		}
		return result;
	}

	public static void Url302(String url,String file) {
		HttpURLConnection conn = null;
		try {
			URL serverUrl = new URL(url);
			conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setRequestMethod("GET");
			// 必须设置false，否则会自动redirect到Location的地址
			conn.setInstanceFollowRedirects(false);
			conn.addRequestProperty("Accept-Charset", "UTF-8;");
			conn.addRequestProperty("User-Agent",USER_AGENT);
			conn.connect();
			String location = conn.getHeaderField("Location");
			Log.debug("URL-302",location);
			downUrl(location,file);
		} catch (Exception e) {
			Log.warn("Url302",e);
		} finally{
			if(conn != null) conn.disconnect();
		}
	}

	public static void downUrl(String url,String file) {
		FileOutputStream fileOut = null;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try{
			File filepath=new File(file).getParentFile();
			if (!filepath.exists())filepath.mkdirs();
			URL httpUrl=new URL(url);
			conn=(HttpURLConnection) httpUrl.openConnection();
			conn.setDoInput(true);  
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.connect();
			inputStream=conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			fileOut = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fileOut);
			byte[] buf = new byte[4096];
			int length = bis.read(buf);
			while(length != -1){
				bos.write(buf, 0, length);
				length = bis.read(buf);
			}
			bos.close();
			bis.close();
			Log.debug("URL-Down-End");
		} catch (Exception e) {
			Log.error("downUrl",e);
		} finally{
			if(conn != null) conn.disconnect();
		}	 
	}

}