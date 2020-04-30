package extension.util.file;

import java.io.*;
import java.util.*;
import java.text.MessageFormat;
//Java

import extension.data.global.Config;
import extension.data.global.Data;
import extension.util.file.FileUtil;
import extension.util.log.Exceptions;
import extension.util.log.Log;
//GA-Exted

/*
 *  Config.java
 *	Initialization.java
 */
public class LoadConfig {

	private static Object load(String input) throws RuntimeException{
		Properties properties = new Properties();
        InputStreamReader inputStream = FileUtil.File(Data.Plugin_Data_Path).toPath("/Config.ini").readconfig();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
		Object result = properties.get(input);
		//防止使读取无效 CALL..
		
		if (result != null) {
            return result;
        }
		Log.warn("NO KEY- Please check the file",input);
		throw Exceptions.Variable("INVALID_PARAMETER");
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
		Object str = load(input);
		return Integer.parseInt(str.toString());
	}

	public static boolean loadboolean(String input) {
		Object  str = load(input);
		if("on".equalsIgnoreCase(str.toString()) || "true".equalsIgnoreCase(str.toString())) {
            return true;
        }
		return false;
	}

	public static String loadstring(String input) {
		Object str = load(input);
		return str.toString();
	}
}