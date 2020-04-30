package extension.data.global;

import java.text.DecimalFormat;
import static extension.util.file.LoadConfig.*;

public class Config {

	// [服务器]
	public static boolean Server_Networking = true;
	public static boolean Server_Country_CN = false;

	// [服务器语言]
	public static String Server_Language = "en_US";

	// [登录]
	public static int Login_Time = 0;
	public static boolean Login_Radical = false;
	public static boolean Permission_Passing = false;

	// [投票]
	public static boolean Vote_Admin = false;
	public static boolean Vote_New_Player = false;
	public static int Vote_New_Player_Time = 0;

	//
	public static boolean Day_and_night = false;
	public static int Day_Time = 0;
	public static float Night_Time = 0;

	// [建造限制]
	public static boolean Building_Restriction = false;
	public static int Building_Warning_quantity = 0;
	public static int Building_Reject_quantity = 0;

	// [单位限制]
	public static boolean Soldier_Restriction;
	public static int Soldier_Warning_quantity;
	public static int Soldier_Reject_quantity;

	// [邮件]
	private static boolean Mail_Use;
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
	public static boolean Help_Show_unauthorize_content;
	
	//
	public static boolean Baidu_Tr;
	public static String Baidu_ID;
	public static String Baidu_Key;



	public static void LaodConfig() {
		// [服务器]
		Server_Language 										= loadstring("Server_Language");

		// [登录]
		Login_Time 												= loadint("Login_Time");
		Login_Radical 											= loadboolean("Login_Radical");
		Permission_Passing 										= loadboolean("Permission_Passing");
		
		// [投票]
		Vote_Admin 												= loadboolean("Vote.Admin");
		Vote_New_Player 										= loadboolean("Vote.New_Player");
		if(Vote_New_Player) {
            Vote_New_Player_Time 								= loadint("Vote.New_Player.Time")*60;
        }

		// [昼夜变换]
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
			Regular_Reporting 									= loadboolean("Mail_Regular_Reporting");
			if(Regular_Reporting) {
				Regular_Reporting_Time 							= loadint("Mail_Regular_Reporting_Time");
				Regular_Reporting_ToMail 						= loadstring("Mail_Regular_Reporting_ToMail");
			}
		}
		// [显示]
		Maximum_Screen_Display 									= loadint("Maximum_Screen_Display");

		// [Help]
		Help_Show_unauthorize_content 							= loadboolean("Help.Show_unauthorize_content");

		// [百度翻译]
		Baidu_Tr 												= loadboolean("Baidu_Tr");
		if(Baidu_Tr) {
			Baidu_ID 											= loadstring("Baidu_Tr.ID");
			Baidu_Key 											= loadstring("Baidu_Tr.Key");
		}
	}

	private static float dis(int a) {
		DecimalFormat df=new DecimalFormat("0.00");//设置保留位数
		return Float.parseFloat(df.format(1.2f/(a*2)));
	}

}
