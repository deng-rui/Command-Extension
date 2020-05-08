package com.github.dr.extension.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.dr.extension.net.HttpRequest.doGet;
import static com.github.dr.extension.util.IsUtil.notisBlank;
//Java

/**
 * @author Dr
 */
public class StringFilteringUtil {

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

	public static String getkeys(String url,String keys,int numbero,int numbert) {
		String tkk = "";
		String result = doGet(url);
		// 去除返回数据空格
		String text = removeAllisBlank(result);
		if (notisBlank(result)) {
			String matchString = findMatchString(text, keys);
			// 提取目标
			tkk = matchString.substring(numbero, matchString.length() - numbert);
		}
		return tkk;
	}

	private static String findFristGroup(Matcher matcher) {
		matcher.find();
		return matcher.group(0);
	}

	public static String removeAllisBlank(String s){
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


    public static String removeAllEn(String s){
		String result = "";
		if(null!=s && !"".equals(s)){
			result = s.replaceAll("[^(A-Za-z)]", "");
		}
		return result;
	}


    public static String removeAllCn(String s){
		String result = "";
		if(null!=s && !"".equals(s)){
			result = s.replaceAll("[^(\\u4e00-\\u9fa5)]", "");
		}
		return result;
	}
}