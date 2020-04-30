package extension.data.global;

import java.text.DecimalFormat;
import static extension.util.file.LoadConfig.*;

public class Config {

	// [服务器]
	public static boolean SERVER_NETWORKING = true;
	public static boolean SERVER_COUNTRY_CN = false;

	// [服务器语言]
	public static String SERVER_LANGUAGE = "en_US";

	// [登录]
	public static int LOGIN_TIME = 0;
	public static boolean LOGIN_RADICAL = false;
	public static boolean PERMISSION_PASSING = false;

	// [投票]
	public static boolean VOTE_ADMIN = false;
	public static boolean VOTE_NEW_PLAYER = false;
	public static int VOTE_NEW_PLAYER_TIME = 0;

	//
	public static boolean DAY_AND_NIGHT = false;
	public static int DAY_TIME = 0;
	public static float NIGHT_TIME = 0;

	// [建造限制]
	public static boolean Building_Restriction = false;
	public static int Building_Warning_quantity = 0;
	public static int Building_Reject_quantity = 0;

	// [单位限制]
	public static boolean Soldier_Restriction = false;
	public static int Soldier_Warning_quantity = 0;
	public static int Soldier_Reject_quantity = 0;

	// [邮件]
	private static boolean Mail_Use = false;
	public static String Mail_SMTP_IP = null;
	public static String Mail_SMTP_Port = null;
	public static String Mail_SMTP_User = null;
	public static String Mail_SMTP_Passwd = null;
	public static boolean Regular_Reporting = false;
	public static int Regular_Reporting_Time = 0;
	public static String Regular_Reporting_ToMail = null;

	// [Display]
	public static int Maximum_Screen_Display = 0;

	// [Help]
	public static boolean Help_Show_unauthorize_content = false;
	
	//
	public static boolean Baidu_Tr = false;
	public static String Baidu_ID = null;
	public static String Baidu_Key = null;




    public static void laodConfig() {
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
    	//设置保留位数
		DecimalFormat df=new DecimalFormat("0.00");
		return Float.parseFloat(df.format(1.2f/(a*2)));
	}

}
