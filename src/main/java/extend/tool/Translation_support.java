package extension.extend.tool;

import com.alibaba.fastjson.JSONArray;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.net.Proxy;
import java.net.URI;
import java.util.Map;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Translation_support {

	public static boolean isBlank(String string) {
		if (string == null || "".equals(string.trim())) {
			return true;
		}
		return false;
	}

	public static boolean isNotBlank(String string) {
		return !isBlank(string);
	}

	public static String extractByStartAndEnd(String str, String startStr, String endStr) {
		String regEx = startStr + ".*?"+endStr;
		String group = findMatchString(str, regEx);
		String trim = group.replace(startStr, "").replace(endStr, "").trim();
		return trim(trim);
	}

	public static String findMatchString(String str, String regEx) {
		try {
			Pattern pattern = Pattern.compile(regEx);
			Matcher matcher = pattern.matcher(str);
			return findFristGroup(matcher);
		} catch (Exception e) {
			e.printStackTrace();
		return null;
		}
	}

	private static String findFristGroup(Matcher matcher) {
		matcher.find();
		return matcher.group(0);
	}

	public static String removeAllBlank(String s){
		String result = "";
		if(null!=s && !"".equals(s)){
			result = s.replaceAll("[　*| *| *|//s*]*", "");
		}
		return result;
	}

	public static String trim(String s){
		String result = "";
		if(null!=s && !"".equals(s)){
			result = s.replaceAll("^[　*| *| *|//s*]*", "").replaceAll("[　*| *| *|//s*]*$", "");
		}
		return result;
	}

	public static String doGet(String url, Map<String, String> param) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			URIBuilder builder = new URIBuilder(url);
			if (param != null) {
				for (String key : param.keySet()) {
					builder.addParameter(key, (String) param.get(key));
				}
			}
			URI uri = builder.build();

			HttpGet httpGet = new HttpGet(uri);

			response = httpclient.execute(httpGet);

			if (response.getStatusLine().getStatusCode() == 200)
				resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	public static String doGet(String url) {
		return doGet(url, null);
	}

	public static String getkeys(String url,String keys) throws Exception {
		try {
			String result = doGet(url);
			if (isNotBlank(result)) {
				if (result.indexOf(keys) > -1) {
					String matchString = findMatchString(result, keys+".*?',");
					String tkk = matchString.substring(5, matchString.length() - 2);
					return tkk;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
}