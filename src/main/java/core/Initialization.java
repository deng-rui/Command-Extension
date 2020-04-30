package extension.core;

import arc.ApplicationListener;
import arc.Core;
import extension.core.ex.Threads;
import extension.data.global.Config;
import extension.data.global.Data;
import extension.data.global.Lists;
import extension.data.global.Maps;
import extension.net.Net;
import extension.util.LocaleUtil;
import extension.util.file.FileUtil;
import extension.util.log.Log;
import mindustry.maps.Map;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static extension.data.db.SQLite.InitializationSQLite;
import static extension.dependent.Librarydependency.importLib;
import static extension.util.file.LoadConfig.loadstring;
import static mindustry.Vars.maps;

public class Initialization {

    public void Start_Initialization() {
		// 配置文件初始化
		Config();
		// 初始化配置
		Config.laodConfig();
		//Resource();
		//IsCN();
		new Threads();
		//新线程 初始化数据
		Threads.NewThred_SE(() -> SQL());
		// 新线程 初始化权限list
		Threads.NewThred_SE(() -> Player_Privilege_classification());
		
		Maps.setLocale("zh_CN",new LocaleUtil("zh_CN"));
		Maps.setLocale("zh_HK",new LocaleUtil("zh_HK"));
		Maps.setLocale("zh_MO",new LocaleUtil("zh_MO"));
		Maps.setLocale("zh_TW",new LocaleUtil("zh_TW"));
		Maps.setLocale("ru_RU",new LocaleUtil("ru_RU"));
		Maps.setLocale("en_US",new LocaleUtil("en_US"));
	}


    public void Override_Initialization() {
		Cover_Gameover();
	}


    public void ReLoadConfig() {
		Config.LAODCONFIG();
		Player_Privilege_classification();
	}


    private void Cover_Gameover() {
		// 覆盖掉自带的Gameover 使用自己的 便于自动更换模式
		try {    
			ApplicationListener sr = Core.app.getListeners().find(e -> "ServerControl".equalsIgnoreCase(e.getClass().getSimpleName()));
			Field field = sr.getClass().getDeclaredField("inExtraRound");      
			field.setAccessible(true);      
			field.setBoolean(sr, true);      
			field.setAccessible(false);   
		}catch(Exception e){
			Log.fatal("Cover Gameover error",e);
		}        
	}      


    private void SQL() {
		importLib("org.xerial","sqlite-jdbc","3.30.1",Data.PLUGIN_LIB_PATH);
		if(!Core.settings.getDataDirectory().child("mods/GA/Data.db").exists()) {
            InitializationSQLite();
        }
	}


    private void Config() {
		try {
			if(!FileUtil.File(Data.PLUGIN_DATA_PATH).toPath("/Config.ini").exists()) {
				String data = (String)FileUtil.readfile(false,new InputStreamReader(Initialization.class.getResourceAsStream("/Config.ini"), "UTF-8"));
				FileUtil.writefile(data,false);
				Log.info("Defect : Start creating write external Config File",Data.Plugin_Data_Path+"/Config.ini");
			}
		}catch(UnsupportedEncodingException e){
			Log.fatal("File write error",e);
		}  
	}

	// CP出jar特定文件至硬盘
	// 下个版本 弃用 -X
	// 无法便于更新语言文件
    private void Resource() {
		try {
			List file = (List)FileUtil.readfile(true,new InputStreamReader(Initialization.class.getResourceAsStream("/other/FileList.txt"), "UTF-8"));
			for(int i=0,len=file.size();i<len;i++){
				//Log.info((String)file.get(i));
				if(!FileUtil.File(Data.PLUGIN_RESOURCES_PATH+(String)file.get(i)).exists()) {
					//IPR必须加上/
					String a = (String)FileUtil.readfile(false,new InputStreamReader(Initialization.class.getResourceAsStream((String)file.get(i)), "UTF-8"));
					FileUtil.writefile(a,false);
					Log.info("Defect : Start creating write external resource File",Data.PLUGIN_RESOURCES_PATH+(String)file.get(i));
				}
			}
		}catch(UnsupportedEncodingException e){
			Log.fatal("File write error",e);
		}
	}

    public void MapList() {
		Lists.EmptyMaps_List();
		if(!maps.all().isEmpty()){
			for(Map map : maps.all()){
				// 只会加载自定义地图
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
						default:
							break;
					}			
				}
			}
		} else {
			Lists.addMaps_List("The map is empty");
		}
	}

    private void IsNetwork() {
		Config.SERVER_NETWORKING = Net.isConnect();
	}

    private void IsCN() {
		
	}

// RG-1
    private void Player_Privilege_classification() {
		String[] tempstring=loadstring("Privilege_Level").split(",");
		int[] tempint = new int[tempstring.length];
		for (int i = 0,len=tempstring.length;i<len; i++) {
            tempint[i] = Integer.parseInt(tempstring[i]);
        }
		int[] temp = selectionSort(tempint);
		if(Config.PERMISSION_PASSING) {
			for (int i=0,len=temp.length;i<len;i++) {
				final String data = loadstring("Privilege."+temp[i]);
				if (data == null) {
                    continue;
                }
				if (i <= 1) {
					Maps.setPower_Data(i,Arrays.asList(data.split(",")));
					continue;
				}
				List<String> power = new ArrayList<String>(Arrays.asList(data.split(",")));
				power.addAll(Maps.getPower_Data(temp[i-1]));
				List<String> filter = new ArrayList<String>();
				filter.addAll(new HashSet<String>(power));
				Maps.setPower_Data(temp[i],filter);
			}
		} else {
			for (int i=0,len=temp.length;i<len;i++) {
				final String data = loadstring("Privilege."+temp[i]);
				if (data == null) {
                    continue;
                }
				Maps.setPower_Data(temp[i],Arrays.asList(data.split(",")));
			}
		}
	}

	private int[] selectionSort(int[] array) {
		if (array.length == 0) {
            return array;
        }
		for (int i=0,len=array.length;i<len;i++) {
			int minIndex = i;
			for (int j=i,len1=array.length;j<len1;j++) {
				if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
			}
			int temp = array[minIndex];
			array[minIndex] = array[i];
			array[i] = temp;
		}
		return array;
	}
}