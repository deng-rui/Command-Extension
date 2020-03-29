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

import extension.data.global.Config;
import extension.dependent.UTF8Control;
import extension.util.file.FileUtil;
import extension.util.Log;
//GA-Exted

import static extension.data.json.Json.getData;
//GA-Exted

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//Json

import extension.util.Log;
 
public class LocaleUtil {

	public static String language(String o,String t,String input,Object[] params) throws MalformedURLException {
		
		Locale locale = new Locale(o,t);
		URLClassLoader file = new URLClassLoader(new URL[] {new File(FileUtil.File(Config.Plugin_Resources_bundles_Path).getPath()).toURI().toURL()});
		ResourceBundle bundle = ResourceBundle.getBundle("GA", locale, file, new UTF8Control());
		//UTF-8 外置资源
		//ResourceBundle bundle = ResourceBundle.getBundle("bundles/GA", locale, new UTF8Control());
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
		JSONObject date = getData("mods/GA/Setting.json");
		try {
			String o = (String) date.get("languageO");
			String t = (String) date.get("languageT");
			if (params == null) return language(o,t,input,null);
			Object[] ps = new Object[params.length];
			for (int i=0;i<params.length;i++) ps[i] = params[i];
			return language(o,t,input,ps);
		} catch (Exception e) {
			Log.error(e);
		}
		return null;
	}

	public static String getinputt(String input,Object[] ps) {
		JSONObject date = getData("mods/GA/Setting.json");
		try {
			String o = (String) date.get("languageO");
			String t = (String) date.get("languageT");
			return language(o,t,input,ps);
		} catch (Exception e) {
			Log.error(e);
		}
		return null;
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

