package extension.auxiliary;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
//Java

import io.anuke.arc.Core;
//Arc

import static extension.extend.Json.getData;
import static extension.extend.tool.unicode.*;
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
		ResourceBundle bundle = ResourceBundle.getBundle("GA-resources/bundles/GA", locale);
		if(input !=null){
			if(p1 == null && p2 == null){
				String result = bundle.getString(input);
				return language1(result,o);
			}else if(p1 != null && p2 == null){
				Object[] params = {p1};
				String result = new MessageFormat(bundle.getString(input),locale).format(params);
				return language1(result,o);
			}else if(p1 != null && p2 != null){
				Object[] params = {p1,p2};
				String result = new MessageFormat(bundle.getString(input),locale).format(params);
				return language1(result,o);
			}
		}
		return null;
	}
	public String language1(String result,String o) {
			if(result != null){
				if(o.equals("zh")){
				return unicodeToString(result);
				}else{
					return result;
				}
			
			}
			return null;
		}
	

	/*
	public String language_zh_CN(String input) throws Exception {
		Locale locale = new Locale("zh", "CN");
		ResourceBundle bundle = ResourceBundle.getBundle("GA-resources/bundles/GA", locale);
		if(input !=null){
			String result = new MessageFormat(bundle.getString(input),locale).format(params);
			if(result != null){
				return result;
			}
		}
	}

	public String language_zh_TW(String input) throws Exception {
		Locale locale = new Locale("zh", "TW");
		ResourceBundle bundle = ResourceBundle.getBundle("GA-resources/bundles/GA", locale);
		if(input !=null){
			String result = new MessageFormat(bundle.getString(input),locale).format(params);
			if(result != null){
				return result;
			}
		}
	}

	public String language_en_US(String input) throws Exception {
		Locale locale = new Locale("en", "US");
		ResourceBundle bundle = ResourceBundle.getBundle("GA-resources/bundles/GA", locale);
		if(input !=null){
			String result = new MessageFormat(bundle.getString(input),locale).format(params);
			if(result != null){
				return result;
			}
		}
	}
	*/
	//I'm so confused myself.

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