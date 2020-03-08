package extension.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//Java

import static extension.net.HttpRequest.doGet;
import static extension.util.RegularUtil.NotBlank;
import static extension.util.String_filteringUtil.removeAllBlank;
import static extension.util.String_filteringUtil.trim;
//Static

public class ExtractUtil {

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

	public static String getkeys(String url,String keys,int numbero,int numbert) throws Exception {
		try {
			String result = doGet(url);
			// 去除返回数据空格
			String text = removeAllBlank(result);
			if (NotBlank(result)) {
					String matchString = findMatchString(text, keys);
					// 提取目标
					String tkk = matchString.substring(numbero, matchString.length() - numbert);
					return tkk;
			}
		} catch (Exception e) {
		}
		return null;
	}

}