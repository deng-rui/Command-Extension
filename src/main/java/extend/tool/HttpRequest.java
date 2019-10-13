package extension.extend.tool;

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
 
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static extension.extend.tool.Tool.isBlank;

public class HttpRequest {

	private static final String GZIP_CONTENT_TYPE = "application/x-gzip";
	private static final String DEFUALT_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
	public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4.4;  en-us; Nexus 4 Build/JOP40D) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2307.2 Mobile Safari/537.36";
	public static final int default_socketTimeout = 10000;
 
	public static String doPost(String reqURL, Map<String, String> params){
		return doPost(reqURL, params, false, null, null);
	}
 
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
 
	public static String doPost(String reqURL, Map<String, String> params, Boolean gzip, String encodeCharset, String decodeCharset) {
		String responseContent = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(reqURL);
		if(gzip){
			httpPost.setHeader(HTTP.CONTENT_TYPE,GZIP_CONTENT_TYPE);
		}
		httpPost.setHeader(HTTP.USER_AGENT, USER_AGENT);
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		Set<Map.Entry<String, String>>  paramSet = params.entrySet();
		if(null != paramSet && paramSet.size() > 0){
			for(Map.Entry<String,String> entry : paramSet){
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, encodeCharset==null ? "UTF-8" : encodeCharset));
 
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(default_socketTimeout).setConnectTimeout(default_socketTimeout).build();//设置请求和传输超时时间
			httpPost.setConfig(requestConfig);
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try{
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					String contentType = "";
					Header[] headers = httpPost.getHeaders(HTTP.CONTENT_TYPE);
					if(headers != null && headers.length>0){
						contentType = headers[0].getValue();
					}
					if(contentType.equalsIgnoreCase(GZIP_CONTENT_TYPE)){
						responseContent = unGZipContent(entity,decodeCharset==null ? "UTF-8" : decodeCharset);
					}else{
						responseContent = EntityUtils.toString(entity, decodeCharset==null ? "UTF-8" : decodeCharset);
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
/*
	public static String doCookie(Map<String, String> map, String charset,String[] str) {
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			CookieStore cookieStore = new BasicCookieStore();
			httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
			httpPost = new HttpPost("http://localhost:8080/testtoolmanagement/LoginServlet");
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> elem = (Entry<String, String>) iterator.next();
				list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
			}
			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
				httpPost.setEntity(entity);
			}
			httpClient.execute(httpPost);
			String JSESSIONID = null;
			String cookie_user = null;
			List<Cookie> cookies = cookieStore.getCookies();
			for (int i = 0; i < cookies.size(); i++) {
				for (int ia = 0; ia < str.size(); ia++) {
					if (cookies.get(i).getName().equals(str[ia])) {
					result = cookies.get(i).getValue();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
*/
 
}