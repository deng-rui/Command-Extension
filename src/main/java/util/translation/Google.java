package extension.util.translation;

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
//Java

import extension.util.LogUtil;
//GA-Exted

import static extension.net.HttpRequest.doGet;
import static extension.util.ExtractUtil.getkeys;
import static extension.util.RegularUtil.Blank;
import static extension.util.RegularUtil.NotBlank;
//Static

import com.alibaba.fastjson.JSONArray;
//Json

public class Google {

	private static ScriptEngine engine = null;

	static {
		ScriptEngineManager maneger = new ScriptEngineManager();
		engine = maneger.getEngineByName("javascript");
		FileInputStream fileInputStream = null;
		Reader scriptReader = null;

		try {
			scriptReader = new InputStreamReader(Google.class.getResourceAsStream("/tk/Google.js"), "utf-8");
			engine.eval(scriptReader);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					LogUtil.warn(e);
				}
			}
			if (scriptReader != null) {
				try {
					scriptReader.close();
				} catch (IOException e) {
					LogUtil.warn(e);
				}
			}
		}
	}

	private static String getTK(String word, String tkk) {
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

	/**
	 * 谷歌翻译：
	 * @param word 待翻译文本
	 * @param from 原语言 默认auto
	 * @param to   目标语言
	 * @return，如果存在，则返回翻译后语言，不存在返回null
	 * @version 1.0
	 */
	public String translate(String word, String from, String to) throws Exception {
		if (Blank(word))return null;
		String tkk = getkeys("https://translate.google.cn/","tkk:.*?',",Integer.parseInt("5"),Integer.parseInt("2"));
		if (Blank(tkk))return null;
		String tk = getTK(word, tkk);
		word = URLEncoder.encode(word, "UTF-8");
		StringBuffer buffer = new StringBuffer("https://translate.google.cn/translate_a/single?client=t");
		if (Blank(from)) {
			from = "auto";
		}
		buffer.append("&sl=" + from);
		buffer.append("&tl=" + to);
		buffer.append("&hl=zh-CN&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&ie=UTF-8&oe=UTF-8&source=btn&kc=0");
		buffer.append("&tk=" + tk);
		buffer.append("&q=" + word);
		String url = buffer.toString();
		String result = doGet(url);
		JSONArray array = (JSONArray) JSONArray.parse(result);
		JSONArray rArray = array.getJSONArray(0);
		StringBuffer rBuffer = new StringBuffer();
		for (int i = 0; i < rArray.size(); i++) {
			String r = rArray.getJSONArray(i).getString(0);
			if (NotBlank(r)) {
				rBuffer.append(r);
				LogUtil.info(r);
			}
		}
		return rBuffer.toString();
	}

	public String translate(String word, String to) throws Exception {
		return translate(word, null, to);
	}
}
