package extension.util;

import java.util.regex.Matcher;
//Java

public class String_filteringUtil {

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