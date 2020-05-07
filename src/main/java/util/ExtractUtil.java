package extension.util;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dr
 */
public class ExtractUtil {

	final static Pattern PATTERN = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");

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
        StringBuilder unicodeBytes = new StringBuilder();
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
			unicodeBytes.append("\\u").append(hexB);
        }
        return unicodeBytes.toString();
    }

    public static String unicodeDecode(String string) {
		String str = string;
		Matcher matcher = PATTERN.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

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

    public static String stringToUtf8(String string) throws UnsupportedEncodingException {
    	return new String(string.getBytes("ISO-8859-1"), extension.data.global.Data.UTF_8);
    }

}