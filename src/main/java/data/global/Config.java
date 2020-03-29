package extension.data.global;

import static extension.util.file.LoadConfig.loadint;
import static extension.util.file.LoadConfig.loadstring;
import static extension.util.file.LoadConfig.loadboolean;

public class Config {

	public static final String Plugin_Path 						= "/config/mods";
	public static final String Plugin_Data_Path 				= "/config/mods/GA";
	public static final String Plugin_Lib_Path 					= "/config/mods/GA/lib";
	public static final String Plugin_Log_Path 					= "/config/mods/GA/log";
	public static final String Plugin_Resources_Path 			= "/config/mods/GA/resources";
	public static final String Plugin_Resources_bundles_Path 	= "/config/mods/GA/resources/bundles";
	public static final String Plugin_Resources_Other_Path 		= "/config/mods/GA/resources/other";

	public static boolean Server_Networking 					= false;
	public static boolean Server_Country_CN 					= false;

	public static boolean Permission_Passing 					= false;

	public static boolean Building_Restriction 					= false;
	public static int Warning_quantity 							= 0;
	public static int Reject_quantity 							= 0;

	public static boolean Mail_Use 								= false;
	public static String Mail_SMTP_IP 							= null;
	public static String Mail_SMTP_Port 						= null;
	public static String Mail_SMTP_User 						= null;
	public static String Mail_SMTP_Passwd 							= null;
	public static boolean Regular_Reporting						= false;
	public static int Regular_Reporting_Time 					= 0;
	public static String Regular_Reporting_ToMail 				= null;




	public static void LaodConfig() {
		Permission_Passing 										= loadboolean("Permission_Passing");
		Building_Restriction 									= loadboolean("Building_Restriction");
		Mail_Use 												= loadboolean("Mail_Use");
		if(Building_Restriction) {
			Warning_quantity 									= loadint("Wan_Construction");
			Reject_quantity 									= loadint("MAX_Construction");
		}
		if(Mail_Use) {
			Mail_SMTP_IP 										= loadstring("Mail_SMTP.IP");
			Mail_SMTP_Port 										= loadstring("Mail_SMTP.Port");
			Mail_SMTP_User 										= loadstring("Mail_SMTP.User");
			Mail_SMTP_Passwd 									= loadstring("Mail_SMTP.Passwd");
			Regular_Reporting 									= loadboolean("Regular_Reporting");
			if(Regular_Reporting) {
				Regular_Reporting_Time 							= loadint("Regular_Reporting_Time")/5;
				Regular_Reporting_ToMail 						= loadstring("Regular_Reporting_ToMail");
			}
		}
	}

}
