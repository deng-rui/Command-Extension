package extension.extend.tool;

import arc.Core;
//Arc

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
//Json

public class Json {
	//JSONObject db = "/GA-resources/zh-CN.json";

	public static void addjson() {
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

	public static JSONObject getData(String fill){
		JSONTokener parser = new JSONTokener(fill);
		JSONObject object = new JSONObject(parser);
		return object;
	}
}