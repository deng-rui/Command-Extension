package extension.util.translation;

import com.alibaba.fastjson.JSONArray;
import extension.util.log.Log;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.net.URLEncoder;

import static extension.net.HttpRequest.doGet;
import static extension.util.ExtractUtil.getkeys;
import static extension.util.IsUtil.isBlank;
import static extension.util.IsUtil.notisBlank;

//Java
//GA-Exted
//Static
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
					Log.warn(e);
				}
			}
			if (scriptReader != null) {
				try {
					scriptReader.close();
				} catch (IOException e) {
					Log.warn(e);
				}
			}
		}
	}


    private static String getTk(String word, String tkk) {
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
	public String translate(String word, String from, String to) {
		if (isBlank(word)) {
            return null;
        }
		String tkk = getkeys("https://translate.google.cn/","tkk:.*?',",5,2);
		if (isBlank(tkk)) {
            return null;
        }
		String tk = getTk(word, tkk);
		try {
			word = URLEncoder.encode(word, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		StringBuffer buffer = new StringBuffer("https://translate.google.cn/translate_a/single?client=t");
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
			if (notisBlank(r)) {
				rBuffer.append(r);
			}
		}
		return rBuffer.toString();
	}

	public String translate(String word, String to) {
		return translate(word,"auto",to);
	}
}
