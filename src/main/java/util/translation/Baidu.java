package extension.util.translation;

// 需要KEY+ID
// 无法白嫖

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import extension.data.global.Config;
import extension.util.encryption.MD5;

import static extension.net.HttpRequest.doPost;
import static extension.util.ExtractUtil.unicodeDecode;
import static extension.util.IsUtil.NotBlank;

//
//
 
public class Baidu {

	public String translate(String query, String from, String to) {
		StringBuffer sb = new StringBuffer();
		String salt = String.valueOf(System.currentTimeMillis());
		String result = null;
		sb.append("q=" + query)
			.append("&from=" + from)
			.append("&to=" + to)
			.append("&appid=" + Config.Baidu_ID)
		// 随机数
		.append("&salt=" + salt)
		// 签名
		.append("&sign=" + MD5.md5(Config.Baidu_ID + query + salt + Config.Baidu_Key));
		JSONObject json = JSON.parseObject(doPost("https://api.fanyi.baidu.com/api/trans/vip/translate",sb.toString()));
		JSONArray rArray = json.getJSONArray("trans_result");
		for (int i = 0; i < rArray.size(); i++) {
			JSONObject r = (JSONObject)rArray.get(i);
			if (NotBlank(r)) {
                result = r.getString("dst");
            }
		}
		return unicodeDecode(result);
	}

	public String translate(String query, String to) {
		return translate(query,"auto",to);
	}

}
