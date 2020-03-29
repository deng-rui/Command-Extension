package extension.core;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Arrays;
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
//Static

import com.alibaba.fastjson.JSONObject;
//Json

public class Initialization {
	public static void Start_Initialization() {
		Config();
		Resource();
		new Config();
		IsNetwork();
		IsCN();
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
	try {    
		ApplicationListener sr = Core.app.getListeners().find(e -> e.getClass().getSimpleName().equalsIgnoreCase("ServerControl"));
		Field field = sr.getClass().getDeclaredField("inExtraRound");      
		field.setAccessible(true);      
		field.setBoolean(sr, true);      
		field.setAccessible(false);   
		}catch(Exception e){
			Log.fatal("File write error",e);
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

	private static void IsCN() {
		
	}

	private static void IsNetwork() {
		Log.info("ST",Config.Server_Networking);
		Config.Server_Networking = Net.isConnect();
		Log.info("ED",Config.Server_Networking);
	}

	private static void Player_Privilege_classification() {
		JSONObject date = getData("mods/GA/Authority.json");
		for (int i = 0; i < 11; i++) {
			Maps.setPower_Data(i,(List)date.get(i));
		}
	}
}