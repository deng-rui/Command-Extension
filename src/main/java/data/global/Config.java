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
	public static boolean BUILDING_RESTRICTION = false;
	public static int BUILDING_WARNING_QUANTITY = 0;
	public static int BUILDING_REJECT_QUANTITY = 0;

	// [单位限制]
	public static boolean SOLDIER_RESTRICTION = false;
	public static int SOLDIER_WARNING_QUANTITY = 0;
	public static int SOLDIER_REJECT_QUANTITY = 0;

	// [邮件]
	private static boolean MAIL_USE = false;
	public static String MAIL_SMTP_IP = null;
	public static String MAIL_SMTP_PORT = null;
	public static String MAIL_SMTP_USER = null;
	public static String MAIL_SMTP_PASSWD = null;
	public static boolean REGULAR_REPORTING = false;
	public static int REGULAR_REPORTING_TIME = 0;
	public static String REGULAR_REPORTING_TOMAIL = null;

	// [Display]
	public static int MAXIMUM_SCREEN_DISPLAY = 0;

	// [Help]
	public static boolean HELP_SHOW_UNAUTHORIZE_CONTENT = false;
	
	//
	public static boolean BAIDU_TR = false;
	public static String BAIDU_ID = null;
	public static String BAIDU_KEY = null;




    public static void laodConfig() {
		// [服务器]
		SERVER_LANGUAGE 										= loadstring("Server_Language");

		// [登录]
		LOGIN_TIME 												= loadint("Login_Time");
		LOGIN_RADICAL 											= loadboolean("Login_Radical");
		PERMISSION_PASSING 										= loadboolean("Permission_Passing");
		
		// [投票]
		VOTE_ADMIN 												= loadboolean("Vote.Admin");
		VOTE_NEW_PLAYER 										= loadboolean("Vote.New_Player");
		if(VOTE_NEW_PLAYER) {
            VOTE_NEW_PLAYER_TIME 								= loadint("Vote.New_Player.Time")*60;
        }

		// [昼夜变换]
		DAY_AND_NIGHT 											= loadboolean("Day_and_night");
		if(DAY_AND_NIGHT) {
			DAY_TIME 											= loadint("Day.Time");
			NIGHT_TIME 											= dis(loadint("Night.Time"));
		}

		// [建造限制]
		BUILDING_RESTRICTION 									= loadboolean("Building_Restriction");
		if(BUILDING_RESTRICTION) {
			BUILDING_WARNING_QUANTITY 							= loadint("Building_Wan_Construction");
			BUILDING_REJECT_QUANTITY 							= loadint("Building_Max_Construction");
		}

		// [单位限制]
		SOLDIER_RESTRICTION 									= loadboolean("Soldier_Restriction");
		if(SOLDIER_RESTRICTION) {
			SOLDIER_WARNING_QUANTITY 							= loadint("Soldier_Wan_Construction");
			SOLDIER_REJECT_QUANTITY 							= loadint("Soldier_Max_Construction");
		}

		// [邮件]
		MAIL_USE 												= loadboolean("Mail_Use");
		if(MAIL_USE) {
			MAIL_SMTP_IP 										= loadstring("Mail_SMTP.IP");
			MAIL_SMTP_PORT 										= loadstring("Mail_SMTP.Port");
			MAIL_SMTP_USER 										= loadstring("Mail_SMTP.User");
			MAIL_SMTP_PASSWD 									= loadstring("Mail_SMTP.Passwd");
			REGULAR_REPORTING 									= loadboolean("Mail_Regular_Reporting");
			if(REGULAR_REPORTING) {
				REGULAR_REPORTING_TIME 							= loadint("Mail_Regular_Reporting_Time");
				REGULAR_REPORTING_TOMAIL 						= loadstring("Mail_Regular_Reporting_ToMail");
			}
		}
		// [显示]
		MAXIMUM_SCREEN_DISPLAY 									= loadint("Maximum_Screen_Display");

		// [Help]
		HELP_SHOW_UNAUTHORIZE_CONTENT 							= loadboolean("Help.Show_unauthorize_content");

		// [百度翻译]
		BAIDU_TR 												= loadboolean("Baidu_Tr");
		if(BAIDU_TR) {
			BAIDU_ID 											= loadstring("Baidu_Tr.ID");
			BAIDU_KEY 											= loadstring("Baidu_Tr.Key");
		}
	}

	private static float dis(int a) {
    	//设置保留位数
		DecimalFormat df=new DecimalFormat("0.00");
		return Float.parseFloat(df.format(1.2f/(a*2)));
	}

}
