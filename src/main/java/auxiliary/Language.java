package extension.auxiliary;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
//Java

import arc.Core;
//Arc

import extension.auxiliary.UTF8Control;

import static extension.tool.Json.getData;
//GA-Exted

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
//Json
 
public class Language {

	//private final static String file_en_US="GA-resources/bundles/en_US.properties";
	//private static String file="GA-resources.bundles.info";
	//private static Locale locale = new Locale("en", "US");
	//private static ResourceBundle bundle = ResourceBundle.getBundle("GA-resources/bundles/GA", locale);
	public String getinput(String input) {
		return getinput(input,null,null);
	}

	public String getinput(String input,String p1) {
		return getinput(input,p1,null);
	}

/*
	public String getinput(String input,String p1,String p2) {
		return getinput(input,p1,p2);
	}
*/

	public String language(String o,String t,String input,String p1,String p2) throws Exception {
		
		Locale locale = new Locale(o,t);
		ResourceBundle bundle = ResourceBundle.getBundle("GA-resources/bundles/GA", locale, new UTF8Control());
		//UTF-8 害死人.jpg

		if(input !=null){
			if(p1 == null && p2 == null){
				String result = bundle.getString(input);;
				//bundle.getString(input)
				return result;
			}else if(p1 != null && p2 == null){
				Object[] params = {p1};
				String result = new MessageFormat(bundle.getString(input),locale).format(params);
				return result;
			}else if(p1 != null && p2 != null){
				Object[] params = {p1,p2};
				String result = new MessageFormat(bundle.getString(input),locale).format(params);
				return result;
			}
		}
		return null;
	}


	public String getinput(String input,String p1,String p2) {
		JSONObject date = getData("mods/GA/setting.json");
		try{
			String o = (String) date.get("languageO");
			String t = (String) date.get("languageT");
			return language(o,t,input,p1,p2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/*
	public void language() {
		try{
		language(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
}

