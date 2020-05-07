package extension.util.translation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static extension.net.HttpRequest.doPost;
import static extension.util.IsUtil.isBlank;
import static extension.util.IsUtil.notisBlank;
import static extension.util.StringFilteringUtil.getkeys;

public class Bing {

	/**
	 * 谷歌翻译：
	 * @param word 待翻译文本
	 * @param from 原语言 默认auto
	 * @param to   目标语言
	 * @return，如果存在，则返回翻译后语言，不存在返回null
	 * @version 1.0
	 */
	/*
	 * Warn : !!!!!
	 * CN : https://cn.bing.com/
	 * ELSE : https://www.bing.com/
	 */

    public String translate(String word, String from, String to) {
		if (isBlank(word)) {
            return null;
        }
		StringBuffer result = new StringBuffer();
		String lg = getkeys("https://cn.bing.com/translator","IG:.*?,",4,2);
		// 我遇上假Bing了????
		try {
			word = URLEncoder.encode(word, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		StringBuffer url = new StringBuffer("https://cn.bing.com/ttranslatev3?isVertical=1");
		StringBuffer post = new StringBuffer();
		url.append("&&IG=" + lg);
		url.append("&IID=translator.5028.2");
		post.append("fromLang=" + from);
		post.append("&text=" + word);
		post.append("&to=" + to);
		String urll = url.toString();
		String postResult = doPost(urll,post.toString());
		JSONArray array = (JSONArray) JSON.parse(postResult);
		JSONArray arrayy = (JSONArray) JSON.parse(array.getJSONObject(0).getString("translations"));
		for (int i = 0; i < arrayy.size(); i++) {
		JSONObject re = arrayy.getJSONObject(i);
		String r = re.getString("text");
			if (notisBlank(r)) {
				result.append(r);
			}
		}
		return result.toString();
			
	}

	public String translate(String word, String to) {
		return translate(word,"auto-detect",to);
	}
}
