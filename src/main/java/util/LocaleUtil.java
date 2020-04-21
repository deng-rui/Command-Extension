package extension.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
//Java

import extension.data.db.PlayerData;
import extension.data.global.Config;
import extension.data.global.Maps;
import extension.dependent.UTF8Control;
import extension.util.file.FileUtil;
import extension.util.log.Log;
//GA-Exted

import static extension.data.json.Json.getData;
import static extension.util.IsUtil.NotBlank;
//GA-Exted

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//Json
 
public class LocaleUtil {

	public static String language(String o,String t,String input,Object[] params) {
		
		Locale locale = new Locale(o,t);
		//URLClassLoader file = new URLClassLoader(new URL[] {new File(FileUtil.File(Config.Plugin_Resources_bundles_Path).getPath()).toURI().toURL()});
		//ResourceBundle bundle = ResourceBundle.getBundle("GA", locale, file, new UTF8Control());
		//UTF-8 外置资源
		ResourceBundle bundle = ResourceBundle.getBundle("bundles/GA", locale, new UTF8Control());	
		try {
			if (input !=null){
				if (params == null){
					String result = bundle.getString(input);
					return result;
				}else{
					String result = new MessageFormat(bundle.getString(input),locale).format(params);
					return result;
				}
			}
		//防止使游戏崩溃 CALL..
		} catch (MissingResourceException e) {
			Log.error(e);
		}
		return input+" : Key is invalid.";
		
	}

	public static String getinput(String input,Object... params) {
		if (params == null) return getinputts(null,input,null);
		Object[] ps = new Object[params.length];
		for (int i=0;i<params.length;i++) ps[i] = params[i];
		return getinputts(null,input,ps);
	}

	public static String getinputt(String input,Object[] ps) {
		return getinputts(null,input,ps);
	}

	public static String getinputs(String uuid,String input,Object... params) {
		if (params == null) return getinputts(null,input,null);
		Object[] ps = new Object[params.length];
		for (int i=0;i<params.length;i++) ps[i] = params[i];
		return getinputts(uuid,input,ps);
	}

	// 暂时并行
	// 我还未想好到底如何实现 help :(
	public static String getinputts(String uuid,String input,Object[] params) {
		String[] lang = null;
		if(NotBlank(uuid)) {
			PlayerData playerdata = Maps.getPlayer_Data(uuid);
			lang = playerdata.Language.split("_");
		} else {
			lang = Config.Server_Language.split("_");
		}
		if (params == null) return language(lang[0],lang[1],input,null);
		return language(lang[0],lang[1],input,params);
	}

	public static String Language_determination(String string) {
		switch(string){
			case "China" :return "zh_CN";
			case "Hong Kong" :return "zh_HK";
			case "Macao" :return "zh_MO";
			case "Taiwan" :return "zh_TW";
			case "Russia" :return "ru_RU";
			default :return "en_US";
			//I didn't find a better way....
		}
	}

}

