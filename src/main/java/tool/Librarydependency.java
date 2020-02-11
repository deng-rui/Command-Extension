package extension.tool;

import arc.files.Fi;

import java.io.*;
import java.net.*;
import java.util.*;

import static extension.tool.HttpRequest.Url302;
import static extension.tool.HttpRequest.downUrl;

public class Librarydependency {

	private static String url;

	public static void downLoadFromUrl(String str, String name, String version, String country, Fi savePath) {
		String[] temp=str.split("\\.");
		url = "/";
		for (int i = 0; i < temp.length; i++) {
			System.out.println(temp[i]);
			url = url+temp[i]+"/";
		}

		if("China".equalsIgnoreCase(country)) {
			url = "https://maven.aliyun.com/nexus/content/groups/public"+url+name+"/"+version+"/"+name+"-"+version+".jar";
			Url302(url,name+"_"+version+".jar",savePath);
			//ali 302刺激 CN
		}else{
			url = "https://repo1.maven.org/maven2"+url+name+"/"+version+"/"+name+"-"+version+".jar";
			downUrl(url,name+"_"+version+".jar",savePath);
			//maven NO CN	
		}
	}

}