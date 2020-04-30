package extension.net;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

import extension.data.global.Config;
import extension.util.file.FileUtil;
import extension.util.log.Exceptions;
import extension.util.log.Log;
//GA-Exted

import static extension.util.IsUtil.NotBlank;

public class HttpRequest {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36";
	private static final String USER_TESTS = "Mozilla/5.0 (JAVA 11; x64) HI JAVA TO WEB";

	public static String doGet(String url) {
		HttpURLConnection con = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		String line = null;
		try {
			URL conn = new URL(url);
			con = (HttpURLConnection) conn.openConnection();
			con.setConnectTimeout(3000);
			con.setReadTimeout(3000);
			con.setRequestMethod("GET");
			con.addRequestProperty("Accept-Charset", "UTF-8");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Encoding", "gzip,deflate");
			int responseCode = con.getResponseCode();
			String contentEncoding = con.getContentEncoding(); 
			Log.info(contentEncoding);
			if (null != contentEncoding && contentEncoding.indexOf("gzip") != -1) { 
				GZIPInputStream gZIPInputStream = new GZIPInputStream(con.getInputStream());
				in = new BufferedReader(new InputStreamReader(gZIPInputStream,"utf-8"));
				while ((line = in.readLine()) != null) {
                    result.append(new String(line.getBytes("UTF-8")));
                }
			} else {
				in = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
				while ((line = in.readLine()) != null) {
                    result.append("\n"+line);
                }
			}
		} catch (IOException e) {
			Log.error("doGet!",e);
		} finally{
			if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    in = null;
                }
            }
			if(con != null) {
                con.disconnect();
            }
		}
		return result.toString();
	}

	public static String doPost(String url, String param) {
		StringBuilder result = new StringBuilder();
		PrintWriter out = null;
		BufferedReader in = null;
		String line = null;
		try{
			URL realUrl = new URL(url);
			URLConnection conn =  realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent",USER_AGENT);
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.flush();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			String contentEncoding = conn.getContentEncoding(); 
			if (null != contentEncoding && contentEncoding.indexOf("gzip") != -1) { 
				GZIPInputStream gZIPInputStream = new GZIPInputStream(conn.getInputStream());
				in = new BufferedReader(new InputStreamReader(gZIPInputStream,"utf-8"));
	            while ((line = in.readLine()) != null) {
                    result.append(new String(line.getBytes("UTF-8")));
                }
			} else {
				in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
				while ((line = in.readLine()) != null) {
                    result.append("\n"+line);
                }
			}
		} catch (IOException e) {
			Log.error("doPost!",e);
		} finally{
			if(out != null) {
                out.close();
            }
			if(in != null) {
                try{
                    in.close();
                } catch (IOException e) {
                    in = null;
                }
            }
		}
		return result.toString();
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
		} catch (IOException e) {
			Log.error("Url302",e);
		} finally{
			if(conn != null) {
                conn.disconnect();
            }
		}
	}

	public static void downUrl(String url,String file) {
		FileOutputStream fileOut = null;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try{
			File filepath=new File(file).getParentFile();
			if(!filepath.exists()) {
                filepath.mkdirs();
            }
			URL httpUrl=new URL(url);
			conn=(HttpURLConnection) httpUrl.openConnection();
			conn.setDoInput(true);  
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.connect();
			inputStream=conn.getInputStream();
			bis = new BufferedInputStream(inputStream);
			fileOut = new FileOutputStream(file);
			bos = new BufferedOutputStream(fileOut);
			byte[] buf = new byte[4096];
			int length = bis.read(buf);
			while(length != -1){
				bos.write(buf, 0, length);
				length = bis.read(buf);
			}
			Log.debug("URL-Down-End");
		} catch (IOException e) {
			Log.error("downUrl",e);
		} finally{
			if(conn != null) {
                conn.disconnect();
            }
			if(bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    bos = null;
                }
            }
			if(bis != null) {
                try {
                    bis.close();
                } catch (Exception e) {
                    bis = null;
                }
            }
		}	 
	}

}