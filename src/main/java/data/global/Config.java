package extension.data.global;

import java.text.DecimalFormat;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;

import extension.core.ex.Vote;

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

	// [Cache]
	public static Vote vote;

	// [服务器]
	public static boolean Server_Networking 					= true;
	public static boolean Server_Country_CN 					= false;

	// [服务器语言]
	public static String Server_Language;

	// [登录]
	public static boolean Login;
	public static boolean Login_IP;
	public static int Login_Time;
	public static boolean Login_Radical;
	public static boolean Permission_Passing;

	// [投票]
	public static boolean Vote_Admin;
	public static boolean Vote_New_Player;
	public static int Vote_New_Player_Time;

	//
	public static boolean Day_and_night;
	public static int Day_Time;
	public static float Night_Time;

	// [建造限制]
	public static boolean Building_Restriction;
	public static int Building_Warning_quantity;
	public static int Building_Reject_quantity;

	// [单位限制]
	public static boolean Soldier_Restriction;
	public static int Soldier_Warning_quantity;
	public static int Soldier_Reject_quantity;

	// [邮件]
	public static boolean Mail_Use;
	public static String Mail_SMTP_IP;
	public static String Mail_SMTP_Port;
	public static String Mail_SMTP_User;
	public static String Mail_SMTP_Passwd;
	public static boolean Regular_Reporting;
	public static int Regular_Reporting_Time;
	public static String Regular_Reporting_ToMail;

	// [Display]
	public static int Maximum_Screen_Display;

	// [Help]
	//public static boolean




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
