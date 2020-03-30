package extension.core;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;

//Java

import arc.ApplicationListener;
import arc.Core;
//Arc

import mindustry.maps.Map;
import mindustry.maps.Maps.*;
//Mindustry

import static mindustry.Vars.maps;
//Mindustry-Static

import extension.data.global.Config;
import extension.net.Net;
import extension.core.Threads;
import extension.data.json.Json;
import extension.data.global.Lists;
import extension.data.global.Maps;
import extension.util.Log;
import extension.util.file.FileUtil;
//GA-Exted

import static extension.data.db.SQLite.InitializationSQLite;
import static extension.dependent.Librarydependency.importLib;
import static extension.data.json.Json.getData;
import static extension.util.file.LoadConfig.loadint;
import static extension.util.file.LoadConfig.loadstring;
//Static

import com.alibaba.fastjson.JSONObject;
//Json

public class Initialization {
	public static void Start_Initialization() {
		Config();
		Config.LaodConfig();
		//Resource();
		//IsCN();
		new Thread(new Runnable() {
			@Override
			public void run() {
				SQL();
				Json();
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Player_Privilege_classification();
			}
		}).start();
		
		new Threads();
	}

	public static void Override_Initialization() {
		Cover_Gameover();
	}
 
	private static void Cover_Gameover() { 
		// 覆盖掉自带的Gameover 使用自己的 便于自动更换模式
		try {    
			ApplicationListener sr = Core.app.getListeners().find(e -> e.getClass().getSimpleName().equalsIgnoreCase("ServerControl"));
			Field field = sr.getClass().getDeclaredField("inExtraRound");      
			field.setAccessible(true);      
			field.setBoolean(sr, true);      
			field.setAccessible(false);   
		}catch(Exception e){
			Log.fatal("Cover Gameover error",e);
		}        
	}      


	public static void Follow_up_Initialization() {
		MapList();
	}

	private static void SQL() {
		importLib("org.xerial","sqlite-jdbc","3.30.1",Config.Plugin_Lib_Path);
		if(!Core.settings.getDataDirectory().child("mods/GA/Data.db").exists())InitializationSQLite();
	}

	private static void Json() {
		//if(!Core.settings.getDataDirectory().child("mods/GA/Setting.json").exists())Json.Initialization();
		//if(!Core.settings.getDataDirectory().child("mods/GA/Authority.json").exists())Json.Initialize_permissions();
	}

	private static void Config() {
		try {
			if(!FileUtil.File(Config.Plugin_Data_Path).toPath("/Config.ini").exists()) {
				String data = (String)FileUtil.readfile(false,new InputStreamReader(Initialization.class.getResourceAsStream("/Config.ini"), "UTF-8"));
				Log.info(data);
				FileUtil.writefile(data,false);
				Log.info("Defect : Start creating write external Config File",Config.Plugin_Data_Path+"/Config.ini");
			}
		}catch(UnsupportedEncodingException e){
			Log.fatal("File write error",e);
		}  
	}

	private static void Resource() {
		try {
			List file = (List)FileUtil.readfile(true,new InputStreamReader(Initialization.class.getResourceAsStream("/other/FileList.txt"), "UTF-8"));
			for(int i=0;i<file.size();i++){
				//Log.info((String)file.get(i));
				if(!FileUtil.File(Config.Plugin_Resources_Path+(String)file.get(i)).exists()) {
					//IPR必须加上/
					String a = (String)FileUtil.readfile(false,new InputStreamReader(Initialization.class.getResourceAsStream((String)file.get(i)), "UTF-8"));
					FileUtil.writefile(a,false);
					Log.info("Defect : Start creating write external resource File",Config.Plugin_Resources_Path+(String)file.get(i));
				}
			}
		}catch(UnsupportedEncodingException e){
			Log.fatal("File write error",e);
		}
	}

	public static void MapList() {
		Lists.EmptyMaps_List();
		if(!maps.all().isEmpty()){
			for(Map map : maps.all()){
				if(map.custom) {
					switch(String.valueOf(map.file.name().charAt(0))){
						case "P" :
						case "p" :
							Lists.addMaps_List(map.name().replace(' ', '_')+" "+map.width+"x"+map.height+" pvp"+" P");
							break;
						case "S" :
						case "s" :
							Lists.addMaps_List(map.name().replace(' ', '_')+" "+map.width+"x"+map.height+" survival"+" S");
							break;
						case "A" :
						case "a" :
							Lists.addMaps_List(map.name().replace(' ', '_')+" "+map.width+"x"+map.height+" attack"+" A");
							break;
					}			
				}
			}
		} else {
			Lists.addMaps_List("The map is empty");
		}
	}

	private static void IsNetwork() {
		Config.Server_Networking = Net.isConnect();
	}
	private static void IsCN() {
		
	}

	private static void Player_Privilege_classification() {
		String[] tempstring=loadstring("Privilege_Level").split(",");
		int[] tempint = new int[tempstring.length];
		for (int i = 0; i < tempstring.length; i++) tempint[i] = Integer.parseInt(tempstring[i]);
		int temp[] = selectionSort(tempint);	
		if(Config.Permission_Passing) {
			for (int i = 0; i < temp.length; i++) {
				final String data = loadstring("Privilege."+temp[i]);
				if (data == null) continue;
				if (i <= 1) {
					Maps.setPower_Data(i,Arrays.asList(data.split(",")));
					continue;
				}
				List<String> listtemp = Maps.getPower_Data(temp[i-1]);
				List<String> l = new ArrayList<String>(Arrays.asList(data.split(",")));

				l.addAll(listtemp);
				HashSet<String> h = new HashSet<String>(l); 
				List<String> c = new ArrayList<String>();
				c.addAll(h);
				Maps.setPower_Data(temp[i],c);
			}
		} else {
			for (int i = 0; i < temp.length; i++) {
				final String data = loadstring("Privilege."+temp[i]);
				if (data == null) continue;
				Maps.setPower_Data(temp[i],Arrays.asList(data.split(",")));
			}
		}
	}

	private static int[] selectionSort(int[] array) {
		if (array.length == 0)
			return array;
		for (int i = 0; i < array.length; i++) {
			int minIndex = i;
			for (int j = i; j < array.length; j++) {
				if (array[j] < array[minIndex])
					minIndex = j;
			}
			int temp = array[minIndex];
			array[minIndex] = array[i];
			array[i] = temp;
		}
		return array;
	}
}