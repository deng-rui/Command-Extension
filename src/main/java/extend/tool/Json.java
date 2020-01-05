package extension.extend.tool;

import arc.Core;
//Arc

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
//Json

public class Json {
	//JSONObject db = "/GA-resources/zh-CN.json";

	public static void Initialization() {
		JSONObject add = new JSONObject();
		/*
		add.put("languageO", "en");
		add.put("languageT", "US");
		*/
		add.put("languageO", "zh");
		add.put("languageT", "CN");
		add.put("translateo", false);
		String json = add.toString();
		Core.settings.getDataDirectory().child("mods/GA/setting.json").writeString(json);
	}

	public static JSONObject getData(String path){
		String data = Core.settings.getDataDirectory().child(path).readString();
		JSONTokener parser = new JSONTokener(data);
		JSONObject object = new JSONObject(parser);
		return object;
	}

	public static void InitializationCookie(String path) {
		JSONObject add = new JSONObject();
		add.put("BAIDUID", "");
		add.put("BDtime", "0");
		add.put("qtv", "");
		add.put("qtk", "");
		add.put("TXtime", "0");
		//cookie 有效时间-未实现
		//Baidu translation -TEST阶段
		String json = add.toString();
		Core.settings.getDataDirectory().child(path).writeString(json);
	}


	public static void TEST(String a, String b,String path) {
		JSONObject add = new JSONObject();
		add.put(a, b);
		//cookie 有效时间-未实现
		//Baidu translation -TEST阶段
		String json = add.toString();
		Core.settings.getDataDirectory().child(path).writeString(json);
	}

}