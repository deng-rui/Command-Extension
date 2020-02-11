package extension.tool;

import arc.Core;
import arc.files.Fi;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//alibaba.json

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
//apach.http

import static extension.tool.Tool.isBlank;
import static extension.tool.Json.*;
//

public class HttpRequest {

	private static final String GZIP_CONTENT_TYPE = "application/x-gzip";
	private static final String DEFUALT_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
	public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4.4;  en-us; Nexus 4 Build/JOP40D) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2307.2 Mobile Safari/537.36";
	public static final int default_socketTimeout = 10000;
 
	public static String doGet(String reqURL){
		return doGet(reqURL,null,false);
	}
 
	public static String doGet(String reqURL, String encoding, Boolean inGZIP) {
		long responseLength = 0;
		String responseContent = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(reqURL);
 
		if(inGZIP){
			httpGet.setHeader(HTTP.CONTENT_TYPE,GZIP_CONTENT_TYPE);
		}
		httpGet.setHeader(HTTP.USER_AGENT, USER_AGENT);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(default_socketTimeout).setConnectTimeout(default_socketTimeout).build();
		httpGet.setConfig(requestConfig);
 
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {
				HttpEntity entity = response.getEntity();
				if(null != entity){
					responseLength = entity.getContentLength();
 
					if(inGZIP) {
						responseContent = unGZipContent(entity, encoding == null ? "UTF-8" : encoding);
					} else {
						responseContent = EntityUtils.toString(entity, encoding == null ? "UTF-8" : encoding);
					}
 
					close(entity);
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseContent;
	}

	public static String doPost(String url, String param) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String result = "";
		JSONObject date = getData("mods/GA/cache/Cookie.json");
		String BAIDUID = (String) date.get("BAIDUID");
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = null;
			conn = (HttpURLConnection) realUrl.openConnection();
			// 打开和URL之间的连接
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");    // POST方法
			// 设置通用的请求属性
			//conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Cookie", "BAIDUID="+BAIDUID);
			System.out.println(">>>>>>BAIDUID:"+BAIDUID);
			//conn.setRequestProperty("connection", "Keep-Alive");
			//conn.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
			conn.connect();
			// 获取URLConnection对象对应的输出流
			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			// 发送请求参数
			out.write(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//使用finally块来关闭输出流、输入流
		finally{
			try{
				if(out!=null){
					out.close();
				}
				if(in!=null){
					in.close();
				}
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String unGZipContent(HttpEntity entity,String encoding) throws IOException {
		String responseContent = "";
		GZIPInputStream gis = new GZIPInputStream(entity.getContent());
		int count = 0;
		byte data[] = new byte[1024];
		while ((count = gis.read(data, 0, 1024)) != -1) {
			String str = new String(data, 0, count,encoding);
			responseContent += str;
		}
		return responseContent;
	}
 
	public static ByteArrayOutputStream gZipContent(String sendData) throws IOException{
		if (isBlank(sendData)) {
			return null;
		}
 
		ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
		originalContent.write(sendData.getBytes("UTF-8"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
		originalContent.writeTo(gzipOut);
		gzipOut.close();
		return baos;
	}
 
	public static void close(HttpEntity entity) throws IOException {
		if (entity == null) {
			return;
		}
		if (entity.isStreaming()) {
			final InputStream instream = entity.getContent();
			if (instream != null) {
				instream.close();
			}
		}
	}

	public static String doCookie(String url) {
		RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
		CookieStore cookieStore =  new  BasicCookieStore();
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCookieStore(cookieStore).build();
		CloseableHttpResponse res =  null ;
		try {
			try  {
				HttpGet get =  new  HttpGet(url);
				get.setHeader(HTTP.USER_AGENT, USER_AGENT);
				res = httpClient.execute(get, context);
				for  (Cookie c : cookieStore.getCookies()) {
				   //System.out.println(c.getName() +  ": "  + c.getValue());
					switch(c.getName()) {
						case "BAIDUID":
						//System.out.println(c.getName() +  ": "  + c.getValue());
							if(!Core.settings.getDataDirectory().child("mods/GA/cache/Cookie.json").exists()){
								InitializationCookie("mods/GA/cache/Cookie.json");
								//addData("BDtime",String.valueOf(System.currentTimeMillis())+3600,"mods/GA/cache/Cookie.json");
								return null;
							}
								JSONObject date = getData("mods/GA/cache/Cookie.json");
								date.put("BAIDUID", String.valueOf(c.getValue()));
								Core.settings.getDataDirectory().child("mods/GA/cache/Cookie.json").writeString((String.valueOf(date)));
							break;
						default:
							break;
					}
				}
				res.close();
			}  finally  {
				httpClient.close();
			}
		}  catch (IOException e) {
		e.printStackTrace();
		}
		return null;
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