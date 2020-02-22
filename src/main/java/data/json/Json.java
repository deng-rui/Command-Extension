package extension.data.json;

import arc.Core;
//Arc

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
//Json
//写的越久，BUG越多，伤痕越疼，脾气越差/-活得越久 故事越多 伤痕越疼，脾气越差

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
		String json = JSONObject.toJSONString(date,SerializerFeature.PrettyFormat);
		Core.settings.getDataDirectory().child("mods/GA/setting.json").writeString(json);
		//即将弃用
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
		String json = JSONObject.toJSONString(date,SerializerFeature.PrettyFormat);
		Core.settings.getDataDirectory().child(path).writeString(json);
	}

	public static void Initialize_permissions() {
		/*
		add.put("languageO", "en");
		add.put("languageT", "US");
		*/
		Map<String, List<String>> date = Collections.synchronizedMap(new HashMap<String, List<String>>());
		List<String> a = Collections.synchronizedList(new ArrayList<String>());
		date.put("0", Arrays.asList("login", "register"));
		date.put("1", a);
		date.put("2", a);
		date.put("3", a);
		date.put("4", a);
		date.put("5", a);
		date.put("6", a);
		date.put("7", a);
		date.put("8", a);
		date.put("9", a);
		date.put("10",a);
		String json = JSONObject.toJSONString(date,SerializerFeature.PrettyFormat);
		Core.settings.getDataDirectory().child("mods/GA/Authority.json").writeString(json);
	}


}