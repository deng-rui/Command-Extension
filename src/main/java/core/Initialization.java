package extension.core;

import arc.Core;
//Arc

import static extension.data.db.SQLite.Player_Privilege_classification;
import static extension.data.db.SQLite.InitializationSQLite;
import static extension.data.json.Json.Initialization;
import static extension.data.json.Json.Initialize_permissions;
import static extension.dependent.Librarydependency.importLib;
import static extension.dependent.Librarydependency.notWork;
//Static

public class Initialization {
	public static void Start_Initialization() {
		importLib("org.xerial","sqlite-jdbc","3.30.1",Core.settings.getDataDirectory().child("mods/GA/Lib/"));
		notWork("sqlite-jdbc","3.30.1",Core.settings.getDataDirectory().child("mods/GA/Lib/"));
		//初始化SQL
		if(!Core.settings.getDataDirectory().child("mods/GA/setting.json").exists())Initialization();
		if(!Core.settings.getDataDirectory().child("mods/GA/Authority.json").exists())Initialize_permissions();
		if(!Core.settings.getDataDirectory().child("mods/GA/Data.db").exists())InitializationSQLite();
		//文件....
		Player_Privilege_classification();

	}
}