package extension.data.json;

import arc.Core;
import com.alibaba.fastjson.JSONObject;
//Json
//写的越久，BUG越多，伤痕越疼，脾气越差/-活得越久 故事越多 伤痕越疼，脾气越差

public class Json {

	public static JSONObject getData(String path){
		String data = Core.settings.getDataDirectory().child(path).readString();
		JSONObject object = JSONObject.parseObject(data);
		return object;
	}


}