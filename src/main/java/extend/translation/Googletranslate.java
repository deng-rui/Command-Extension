package extension.extend.translation;

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

import static extension.extend.tool.Translation_support.*;
import static extension.extend.tool.HttpRequest.doGet;
import static extension.extend.tool.Tool.isBlank;
import static extension.extend.tool.Tool.isNotBlank;


public class Googletranslate {

	private static final String PATH = "/GA-resources/tk/Google.js";
	public String url;
	public void setUrl(String url) {
		this.url = url;
	}

//isBlank isNotBlank extractByStartAndEnd findMatchString findFristGroup removeAllBlank trim

	static ScriptEngine engine = null;

	static {
		ScriptEngineManager maneger = new ScriptEngineManager();
		engine = maneger.getEngineByName("javascript");
		FileInputStream fileInputStream = null;
		Reader scriptReader = null;

		try {
			scriptReader = new InputStreamReader(Googletranslate.class.getResourceAsStream(PATH), "utf-8");
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

	public static String getTK(String word, String tkk) {
		String result = null;

		try {
			if (engine instanceof Invocable) {
				Invocable invocable = (Invocable) engine;
				result = (String) invocable.invokeFunction("tk", new Object[]{word, tkk});
			}
		} catch (Exception e) {
		}

		return result;
	}

	public String translate(String word, String from, String to) throws Exception {
		if (isBlank(word)) {
			return null;
		}

		String tkk = getkeys("https://translate.google.cn/","tkk:");

		if (isBlank(tkk)) {
		}

		String tk = getTK(word, tkk);

		try {
			word = URLEncoder.encode(word, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer buffer = new StringBuffer("https://translate.google.cn/translate_a/single?client=t");

		if (isBlank(from)) {
			from = "auto";
		}

		buffer.append("&sl=" + from);
		buffer.append("&tl=" + to);
		buffer.append("&hl=zh-CN&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&ie=UTF-8&oe=UTF-8&source=btn&kc=0");
		buffer.append("&tk=" + tk);
		buffer.append("&q=" + word);
		setUrl(buffer.toString());

		try {
			String result = doGet(this.url);
			JSONArray array = (JSONArray) JSONArray.parse(result);
			JSONArray rArray = array.getJSONArray(0);
			StringBuffer rBuffer = new StringBuffer();
			for (int i = 0; i < rArray.size(); i++) {
				String r = rArray.getJSONArray(i).getString(0);
				if (isNotBlank(r)) {
					rBuffer.append(r);
				}
			}
			return rBuffer.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public String translate(String word, String to) throws Exception {
		return translate(word, null, to);
	}
}
