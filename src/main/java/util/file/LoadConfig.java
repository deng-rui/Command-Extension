package extension.util.file;

import java.io.*;
import java.util.*;
//Java

import extension.data.global.Config;
import extension.util.Log;
import extension.util.file.FileUtil;
//GA-Exted

import extension.util.Log;

public class LoadConfig {

	public static Object load(String input) {
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

	public static int loadint(String input) {
		return Integer.parseInt(load(input).toString());
	}

	public static boolean loadboolean(String input) {
		final String  temp = load(input).toString();
		if(temp.equals("on") || temp.equals("true")) 
			return true;
		return false;
	}
}