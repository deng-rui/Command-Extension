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

	final static Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");

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

	public static String getkeys(String url,String keys,int numbero,int numbert) {
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


    public static boolean inttoBoolean(int in) {
		return (in == 1);
	}


    public static int booleantoInt(boolean bl) {
		if(bl) {
            return 1;
        }
		return 0;
	}

	public static long ipToLong(String strIp) {
		String[]ip = strIp.split("\\.");
		return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);
	}


    public static String longToIp(long longIp) {
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

	public static String unicodeEncode(String string) {
        char[] utfBytes = string.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    public static String unicodeDecode(String string) {
        Matcher matcher = pattern.matcher(string);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            string = string.replace(matcher.group(1), ch + "");
        }
        return string;
    }

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public static String languageDetermination(String string) {
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