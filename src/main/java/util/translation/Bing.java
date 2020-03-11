package extension.util.translation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
//Java

import extension.util.LogUtil;
//GA-Exted

import static extension.net.HttpRequest.doGet;
import static extension.net.HttpRequest.doPost;
import static extension.util.ExtractUtil.getkeys;
import static extension.util.RegularUtil.Blank;
import static extension.util.RegularUtil.NotBlank;
//Static

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
//Json

public class Bing {

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
		StringBuffer result = new StringBuffer();
		String IG = getkeys("https://www.bing.com/translator","IG:.*?,",4,2);
		// 我遇上假Bing了????
		word = URLEncoder.encode(word, "UTF-8");
		StringBuffer url = new StringBuffer("https://www.bing.com/ttranslatev3?isVertical=1");
		StringBuffer post = new StringBuffer();
		if (Blank(from)) from = "auto-detect";
		url.append("&&IG=" + IG);
		url.append("&IID=translator.5028.1");
		post.append("fromLang=" + from);
		post.append("&text=" + word);
		post.append("&to=" + to);
		String urll = url.toString();
		String PostResult = doPost(urll,post.toString());
		JSONArray array = (JSONArray) JSONArray.parse(PostResult);
		JSONArray arrayy = (JSONArray) JSONArray.parse(array.getJSONObject(0).getString("translations"));
        for (int i = 0; i < arrayy.size(); i++) {
            JSONObject re = arrayy.getJSONObject(i);
            String r = re.getString("text");
            if (NotBlank(r)) {
				result.append(r);
			}
        }
		
		return result.toString();
	}

	public String translate(String word, String to) throws Exception {
		return translate(word, null, to);
	}
}
