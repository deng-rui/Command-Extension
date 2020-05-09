package com.github.dr.extension.util.translation;

// 需要KEY+ID
// 无法白嫖

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.dr.extension.data.global.Config;
import com.github.dr.extension.util.encryption.Md5;

import static com.github.dr.extension.net.HttpRequest.doPost;
import static com.github.dr.extension.util.ExtractUtil.unicodeDecode;
import static com.github.dr.extension.util.IsUtil.notisBlank;


/**
 * @author Dr
 * @Date ?
 */
public class Baidu {

	public String translate(String query, String from, String to) {
		StringBuffer sb = new StringBuffer();
		String salt = String.valueOf(System.currentTimeMillis());
		String result = null;
		sb.append("q=" + query)
			.append("&from=" + from)
			.append("&to=" + to)
			.append("&appid=" + Config.BAIDU_ID)
		// 随机数
		.append("&salt=" + salt)
		// 签名
		.append("&sign=" + new Md5().md5(Config.BAIDU_ID + query + salt + Config.BAIDU_KEY));
		JSONObject json = JSON.parseObject(doPost("https://api.fanyi.baidu.com/api/trans/vip/translate",sb.toString()));
		JSONArray rArray = json.getJSONArray("trans_result");
		for (int i = 0; i < rArray.size(); i++) {
			JSONObject r = (JSONObject)rArray.get(i);
			if (notisBlank(r)) {
                result = r.getString("dst");
            }
		}
		return unicodeDecode(result);
	}

	public String translate(String query, String to) {
		return translate(query,"auto",to);
	}

}
