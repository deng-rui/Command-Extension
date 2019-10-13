package extension.extend.tool;

import com.alibaba.fastjson.JSONArray;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.ParseException;

import static extension.extend.tool.HttpRequest.*;
import static extension.extend.tool.Tool.*;

public class Translation_support {

	public static String extractByStartAndEnd(String str, String startStr, String endStr) {
		String regEx = startStr + ".*?"+endStr;
		String group = findMatchString(str, regEx);
		String trim = group.replace(startStr, "").replace(endStr, "").trim();
		return trim(trim);
	}

	public static String findMatchString(String str, String regEx) {
		try {
			Pattern pattern = Pattern.compile(regEx);
			Matcher matcher = pattern.matcher(str);
			return findFristGroup(matcher);
		} catch (Exception e) {
			e.printStackTrace();
		return null;
		}
	}

	private static String findFristGroup(Matcher matcher) {
		matcher.find();
		return matcher.group(0);
	}

	public static String removeAllBlank(String s){
		String result = "";
		if(null!=s && !"".equals(s)){
			result = s.replaceAll("[　*| *| *|//s*]*", "");
		}
		return result;
	}

	public static String trim(String s){
		String result = "";
		if(null!=s && !"".equals(s)){
			result = s.replaceAll("^[　*| *| *|//s*]*", "").replaceAll("[　*| *| *|//s*]*$", "");
		}
		return result;
	}

	public static String getkeys(String url,String keys) throws Exception {
		try {
			String result = doGet(url);
			if (isNotBlank(result)) {
				if (result.indexOf(keys) > -1) {
					String matchString = findMatchString(result, keys+".*?',");
					String tkk = matchString.substring(5, matchString.length() - 2);
					return tkk;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static String getkeys_ljbd(String url,String keys) throws Exception {
		try {
			String result = doGet(url);
			if (isNotBlank(result)) {
				if (result.indexOf(keys) > -1) {
					String matchString = findMatchString(result, keys+".*?';");
					String tkk = matchString.substring(5, matchString.length() - 2);
					return tkk;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
}