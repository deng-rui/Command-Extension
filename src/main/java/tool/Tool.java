package extension.tool;

import java.io.*;
import java.net.*;
import java.util.*;

public class Tool {
	public static boolean Blank(String string) {
		if (string == null || "".equals(string.trim())) {
			return true;
		}
		return false;
	}

	public static boolean NotBlank(String string) {
		return !Blank(string);
	}

	public static String Language_determination(String string) {
		switch(string){
			case "China":
			return "zh_CN";
			case "Hong Kong":
			return "zh_HK";
			case "Macao":
			return "zh_MO";
			case "Taiwan":
			return "zh_TW";
			case "Russia":
			return "ru-RU";
			default :
			return "en_US";
			//I didn't find a better way....
			}
	}
	
}