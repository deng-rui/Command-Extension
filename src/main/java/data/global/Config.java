package extension.data.global;

import java.text.DecimalFormat;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;

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
	// [线程]
	public static final ScheduledExecutorService service 		= Executors.newScheduledThreadPool(5);

	// [服务器]
	public static boolean Server_Networking 					= true;
	public static boolean Server_Country_CN 					= false;

	// [服务器语言]
	public static String Server_Language 						= null;

	// [登录]
	public static boolean Login 								= false;
	public static boolean Login_IP 								= false;
	public static int Login_Time 								= 0;
	public static boolean Login_Radical 						= false;
	public static boolean Permission_Passing 					= false;

	// [投票]
	public static boolean Vote_Admin 							= false;
	public static boolean Vote_New_Player 						= false;
	public static int Vote_New_Player_Time 						= 1;

	//
	public static boolean Day_and_night 						= false;
	public static int Day_Time 									= 0;
	public static float Night_Time 								= 0;

	// [建造限制]
	public static boolean Building_Restriction 					= false;
	public static int Building_Warning_quantity 				= 0;
	public static int Building_Reject_quantity 					= 0;

	// [单位限制]
	public static boolean Soldier_Restriction 					= false;
	public static int Soldier_Warning_quantity 					= 0;
	public static int Soldier_Reject_quantity 					= 0;

	// [邮件]
	public static boolean Mail_Use 								= false;
	public static String Mail_SMTP_IP 							= null;
	public static String Mail_SMTP_Port 						= null;
	public static String Mail_SMTP_User 						= null;
	public static String Mail_SMTP_Passwd 						= null;
	public static boolean Regular_Reporting						= false;
	public static int Regular_Reporting_Time 					= 0;
	public static String Regular_Reporting_ToMail 				= null;




	public static void LaodConfig() {
		// [服务器]
		Server_Language 										= loadstring("Server_Language");

		// [登录]
		Login 													= loadboolean("Login");
		Permission_Passing 										= loadboolean("Permission_Passing");
		if(Login) {
			Login_Time 											= loadint("Login_Time");
			Login_Radical 										= loadboolean("Login_Radical");
		}

		// [投票]
		Vote_Admin 												= loadboolean("Vote.Admin");
		Vote_New_Player 										= loadboolean("Vote.New_Player");
		if(Vote_New_Player) 
			Vote_New_Player_Time 								= loadint("Vote.New_Player.Time")*60;

		//
		Day_and_night 											= loadboolean("Day_and_night");
		if(Day_and_night) {
			Day_Time 											= loadint("Day.Time");
			Night_Time 											= dis(loadint("Night.Time"));
		}

		// [建造限制]
		Building_Restriction 									= loadboolean("Building_Restriction");
		if(Building_Restriction) {
			Building_Warning_quantity 							= loadint("Building_Wan_Construction");
			Building_Reject_quantity 							= loadint("Building_Max_Construction");
		}

		// [单位限制]
		Soldier_Restriction 									= loadboolean("Soldier_Restriction");
		if(Soldier_Restriction) {
			Soldier_Warning_quantity 							= loadint("Soldier_Wan_Construction");
			Soldier_Reject_quantity 							= loadint("Soldier_Max_Construction");
		}

		// [邮件]
		Mail_Use 												= loadboolean("Mail_Use");
		if(Mail_Use) {
			Mail_SMTP_IP 										= loadstring("Mail_SMTP.IP");
			Mail_SMTP_Port 										= loadstring("Mail_SMTP.Port");
			Mail_SMTP_User 										= loadstring("Mail_SMTP.User");
			Mail_SMTP_Passwd 									= loadstring("Mail_SMTP.Passwd");
			Regular_Reporting 									= loadboolean("Regular_Reporting");
			if(Regular_Reporting) {
				Regular_Reporting_Time 							= loadint("Regular_Reporting_Time");
				Regular_Reporting_ToMail 						= loadstring("Regular_Reporting_ToMail");
			}
		}
	}

	private static float dis(int a) {
		DecimalFormat df=new DecimalFormat("0.00");//设置保留位数
		return Float.parseFloat(df.format((float)1/(a*2)));
	}

}
