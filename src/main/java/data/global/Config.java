package extension.data.global;

import static extension.util.file.LoadConfig.loadint;

public class Config {

	public static final String Plugin_Path 						= "/config/mods";
	public static final String Plugin_Data_Path 				= "/config/mods/GA";
	public static final String Plugin_Lib_Path 					= "/config/mods/GA/lib";
	public static final String Plugin_Log_Path 					= "/config/mods/GA/log";
	public static final String Plugin_Resources_Path 			= "/config/mods/GA/resources";
	public static final String Plugin_Resources_bundles_Path 	= "/config/mods/GA/resources/bundles";
	public static final String Plugin_Resources_Other_Path 		= "/config/mods/GA/resources/other";

	public static boolean Server_Country_CN 					= false;
	public static boolean Server_Networking 					= false;

	public static final int Warning_quantity;
	public static final int Reject_quantity;

	static {
		Warning_quantity 	= loadint("Wan_Construction");
		Reject_quantity 	= loadint("MAX_Construction");
	}

}
