package extension.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//Java

import static extension.net.HttpRequest.doGet;
import static extension.util.IsUtil.NotBlank;
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
		String tkk = "";
		String result = doGet(url);
		// 去除返回数据空格
		String text = removeAllBlank(result);
		if (NotBlank(result)) {
			String matchString = findMatchString(text, keys);
			// 提取目标
			tkk = matchString.substring(numbero, matchString.length() - numbert);
			
		}
		return tkk;
	}

	public static boolean InttoBoolean(int in) {
		return (in == 1);
	}

	public static int BooleantoInt(boolean bl) {
		if(bl) return 1;
		return 0;
	}

	public static long ipToLong(String strIp) {
		String[]ip = strIp.split("\\.");
		return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);
	}

	public static String longToIP(long longIp) {
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf((longIp >>> 24)))
		.append(".")
		.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16))
		.append(".")
		.append(String.valueOf((longIp & 0x0000FFFF) >>> 8))
		.append(".")
		.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}

}