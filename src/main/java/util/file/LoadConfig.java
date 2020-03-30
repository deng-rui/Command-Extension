package extension.util.file;

import java.io.*;
import java.util.*;
import java.text.MessageFormat;
//Java

import extension.data.global.Config;
import extension.util.Log;
import extension.util.file.FileUtil;
//GA-Exted

import extension.util.Log;

/*
 *  Config.java
 *	Initialization.java
 */
public class LoadConfig {

	private static Object load(String input) {
		Properties properties = new Properties();
        InputStreamReader inputStream = FileUtil.File(Config.Plugin_Data_Path).toPath("/Config.ini").readconfig();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

		try {
			return properties.get(input);
		//防止使读取无效 CALL..
		} catch (MissingResourceException e) {
			Log.error("NO KEY- Please check the file",e);
		}
		return null;
	}

	public static String CustomLoad(String input,Object[] params) {
		Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(LoadConfig.class.getResourceAsStream("/bundles/GA.properties"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.warn(e);
        } catch (IOException e) {
            Log.warn(e);
        }
		try {
			return new MessageFormat(properties.get(input).toString()).format(params);
		//防止使读取无效 CALL..
		} catch (MissingResourceException e) {
			Log.error("NO KEY- Please check the file",e);
		}
		return null;
	}

	public static int loadint(String input) {
		return Integer.parseInt(load(input).toString());
	}

	public static boolean loadboolean(String input) {
		final String  temp = load(input).toString();
		if(temp.equals("on") || temp.equals("true")) 
			return true;
		return false;
	}

	public static String loadstring(String input) {
		Object str = load(input);
		if (str == null || "".equals(str.toString().trim())) return null;
		return str.toString();
	}
}