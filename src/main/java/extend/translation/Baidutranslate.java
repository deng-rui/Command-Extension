package extension.extend.translation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.ScriptEngineManager;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import static extension.extend.Translation_support.*;
import static extension.tool.HttpRequest.doPost;
import static extension.tool.Tool.isBlank;
import static extension.tool.Tool.isNotBlank;
import static extension.tool.Json.*;



public class Baidutranslate {

	private static final String PATH = "/GA-resources/tk/Baidu.js";
	//Baidu和Google的js一样。
	static ScriptEngine engine = null;

	static {
		ScriptEngineManager maneger = new ScriptEngineManager();
		engine = maneger.getEngineByName("javascript");
		FileInputStream fileInputStream = null;
		Reader scriptReader = null;

		try {
			scriptReader = new InputStreamReader(Baidutranslate.class.getResourceAsStream(PATH), "utf-8");
			engine.eval(scriptReader);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (scriptReader != null) {
				try {
					scriptReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getTK(String word, String gtk) {
		String result = null;

		try {
			if (engine instanceof Invocable) {
				Invocable invocable = (Invocable) engine;
				result = (String) invocable.invokeFunction("token", new Object[]{word, gtk});
			}
		} catch (Exception e) {
		}
		return result;
	}

	public String translate(String word, String from, String to) throws Exception {
		String dst = null;
		try {
			word = URLEncoder.encode(word, "UTF-8");
			System.out.println(">>>>>>word:"+word);
		} catch (Exception e) {
					e.printStackTrace();
		}
		String gtk = getkeys("https://fanyi.baidu.com/","gtk:'.*?'",Integer.parseInt("5"),Integer.parseInt("1"));
		System.out.println(gtk);
		if (isBlank(word))return null;
		//String token = getkeys("https://fanyi.baidu.com/","token:'.*?'",Integer.parseInt("7"),Integer.parseInt("1"));
		String token = "9ce13f0597499da92fec34778ef36d91";
		System.out.println(token);
		//本地获取token过旧，可能是我网络问题:(
		if (isBlank(token))return null;
		String sign = getTK(word, gtk);
		System.out.println(sign);
		if (isBlank(sign))return null;
		String paramMap = "from="+from+"&to="+to+"&query="+word+"&transtype=realtime"+"&simple_means_flag=3"+"&sign="+sign+"&token="+token;
		System.out.println(paramMap);
		String result = doPost("https://fanyi.baidu.com/v2transapi?from=auto&to="+to, paramMap);
		System.out.println(result);

		JSONObject jsonObject = (JSONObject) JSONObject.parseObject(result).getJSONObject("trans_result").getJSONObject("data");
		System.out.println(jsonObject);
			dst = jsonObject.getString("dst");
			System.out.println(dst);
			return dst;  
	}

	public String translate(String word, String to) throws Exception {
		return translate(word, "en", to);
	}

}