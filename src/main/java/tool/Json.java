package extension.tool;

import arc.Core;
//Arc

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//Json
//写的越久，BUG越多，伤痕越疼，脾气越差/-活得越久 故事越多 伤痕越疼，脾气越差
/*
正在进行-临时批注
目的:
全面替换json alibaba.json接替org.json
2020.2.1.21.00
TEMP-
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
*/

public class Json {

	@SuppressWarnings("unchecked")
	public static void Initialization() {
		/*
		add.put("languageO", "en");
		add.put("languageT", "US");
		*/
		Map date = Collections.synchronizedMap(new HashMap());
		date.put("languageO", "zh");
		date.put("languageT", "CN");
		date.put("translateo", false);
		String json = JSONObject.toJSONString(date);
		Core.settings.getDataDirectory().child("mods/GA/setting.json").writeString(json);
	}

	public static JSONObject getData(String path){
		String data = Core.settings.getDataDirectory().child(path).readString();
		JSONObject object = JSONObject.parseObject(data);
		return object;
	}

	public static void InitializationCookie(String path) {
		Map<String, String> date = Collections.synchronizedMap(new HashMap<String, String>());
		date.put("BAIDUID", "");
		date.put("BDtime", "0");
		date.put("qtv", "");
		date.put("qtk", "");
		date.put("TXtime", "0");
		//cookie 有效时间-未实现
		//Baidu translation -TEST阶段
		String json = JSONObject.toJSONString(date);
		Core.settings.getDataDirectory().child(path).writeString(json);
	}

/*
	public static void TEST(String a, String b,String path) {
		JSONObject add = new JSONObject();
		add.put(a, b);
		//cookie 有效时间-未实现
		//Baidu translation -TEST阶段
		String json = add.toString();
		Core.settings.getDataDirectory().child(path).writeString(json);
*/
}