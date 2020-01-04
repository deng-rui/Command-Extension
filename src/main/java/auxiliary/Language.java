package extension.auxiliary;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
//Java

import arc.Core;
//Arc

import extension.auxiliary.UTF8Control;

import static extension.extend.Json.getData;
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
	

	public String language(String o,String t,String input,String p1,String p2) throws Exception {
		
		Locale locale = new Locale(o,t);
		ResourceBundle bundle = ResourceBundle.getBundle("GA-resources/bundles/GA", locale, new UTF8Control());

		//String  = new String(bundle.getString().getBytes("ISO-8859-1"), "UTF-8");
		//String bundle1

		 //bundle1 = bundle.getString(input);
		String AAA = bundle.getString(input);
		//String AAA =  new String(value.getBytes("ISO-8859-1"), "UTF-8");
		//UTF-8 害死人.jpg

		if(input !=null){
			if(p1 == null && p2 == null){
				String result = AAA;
				//bundle.getString(input)
				return result;
			}else if(p1 != null && p2 == null){
				Object[] params = {p1};
				String result = new MessageFormat(AAA,locale).format(params);
				return result;
			}else if(p1 != null && p2 != null){
				Object[] params = {p1,p2};
				String result = new MessageFormat(AAA,locale).format(params);
				return result;
			}
		}
		return null;
	}


	public String getinput(String input,String p1,String p2) {
		JSONObject date = getData();
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

