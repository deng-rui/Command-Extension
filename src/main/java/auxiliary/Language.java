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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//Json
 
public class Language {

	public String getinput(String input) {
		return getinputt(input,null);
	}
	public String getinput(String input,String p0) {
		Object[] params = {p0};
		return getinputt(input,params);
	}
	public String getinput(String input,String p0,String p1) {
		Object[] params = {p0,p1};
		return getinputt(input,params);
	}
	public String getinput(String input,String p0,String p1,String p2) {
		Object[] params = {p0,p1,p2};
		return getinputt(input,params);
	}
	public String getinput(String input,String p0,String p1,String p2,String p3) {
		Object[] params = {p0,p1,p2,p3};
		return getinputt(input,params);
	}
	public String getinput(String input,String p0,String p1,String p2,String p3,String p4) {
		Object[] params = {p0,p1,p2,p3,p4};
		return getinputt(input,params);
	}
	public String getinput(String input,String p0,String p1,String p2,String p3,String p4,String p5) {
		Object[] params = {p0,p1,p2,p3,p4,p5};
		return getinputt(input,params);
	}

	public String language(String o,String t,String input,Object[] params) throws Exception {
		
		Locale locale = new Locale(o,t);
		ResourceBundle bundle = ResourceBundle.getBundle("GA-resources/bundles/GA", locale, new UTF8Control());
		//UTF-8 害死人.jpg
		if(input !=null){
			if(params == null){
				String result = bundle.getString(input);
				return result;
			}else{
				String result = new MessageFormat(bundle.getString(input),locale).format(params);
				return result;
			}
		}
		return null;
	}

	public String getinputt(String input,Object[] params) {
		JSONObject date = getData("mods/GA/setting.json");
		try {
			String o = (String) date.get("languageO");
			String t = (String) date.get("languageT");
			if (params == null) {
   				return language(o,t,input,null);
  			}
			return language(o,t,input,params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

