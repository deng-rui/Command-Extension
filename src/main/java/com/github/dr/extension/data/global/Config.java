package com.github.dr.extension.data.global;

import java.text.DecimalFormat;
import static com.github.dr.extension.util.file.LoadConfig.*;

public class Config {

	/**
	 * 服务器有无网络
	 */
	public static boolean SERVER_NETWORKING = true;

	/**
	 * 服务器归宿地是否为中国
	 */
	public static boolean SERVER_COUNTRY_CN = false;



	/**
	 * 服务器唯一ID
	 */
	public static String SERVER_ID = null;

	/**
	 * 服务器主语言
	 */
	public static String SERVER_LANGUAGE = "en_US";



	/**
	 * 服务器是否使用MARIADB
	 */
	public static boolean SERVER_MARIADB = false;

	/**
	 * MARIADB IP
	 */
	public static String DB_IP = null;

	/**
	 * MARIADB 端口
	 */
	public static String DB_PORT = null;

	/**
	 * MARIADB 数据库名字
	 */
	public static String DB_NAME = null;

	/**
	 * MARIADB 用户名
	 */
	public static String DB_USER = null;

	/**
	 * MARIADB 密码
	 */
	public static String DB_PASSWD = null;



	/**
	 * 登录状态保存时间
	 */
	public static int LOGIN_TIME = 0;

	/**
	 * 激进登陆
	 */
	public static boolean LOGIN_RADICAL = false;

	/**
	 * 权限传递
	 */
	public static boolean PERMISSION_PASSING = false;



	/**
	 * ?-TODO
	 */
	public static boolean VOTE_ADMIN = false;

	/**
	 * 新进玩家可否投票-TODO
	 */
	public static boolean VOTE_NEW_PLAYER = false;

	/**
	 * 新进多少时间后可投-TODO
	 */
	public static int VOTE_NEW_PLAYER_TIME = 0;



	/**
	 * 是否启用昼夜交替
	 */
	public static boolean DAY_AND_NIGHT = false;

	/**
	 * 白天时间
	 */
	public static int DAY_TIME = 0;

	/**
	 * 晚上时间
	 */
	public static float NIGHT_TIME = 0;



	/**
	 * 是否启用建筑限制
	 */
	public static boolean BUILDING_RESTRICTION = false;

	/**
	 * 警告单位
	 */
	public static int BUILDING_WARNING_QUANTITY = 0;

	/**
	 * 最大单位
	 */
	public static int BUILDING_REJECT_QUANTITY = 0;



	/**
	 * 是否启用单位限制
	 */
	public static boolean SOLDIER_RESTRICTION = false;

	/**
	 * 警告单位
	 */
	public static int SOLDIER_WARNING_QUANTITY = 0;

	/**
	 * 最大单位
	 */
	public static int SOLDIER_REJECT_QUANTITY = 0;



	/**
	 * 是否启用MAIL
	 */
	private static boolean MAIL_USE = false;
	public static String MAIL_SMTP_IP = null;
	public static String MAIL_SMTP_PORT = null;
	public static String MAIL_SMTP_USER = null;
	public static String MAIL_SMTP_PASSWD = null;
	public static boolean REGULAR_REPORTING = false;
	public static int REGULAR_REPORTING_TIME = 0;
	public static String REGULAR_REPORTING_TOMAIL = null;



	/**
	 * 列表最大显示
	 */
	public static int MAXIMUM_SCREEN_DISPLAY = 0;



	/**
	 * Help是否显示显示无权限命令
	 */
	public static boolean HELP_SHOW_UNAUTHORIZE_CONTENT = false;
	


	/**
	 * 是否启用百度翻译
	 */
	public static boolean BAIDU_TR = false;

	/**
	 * 百度翻译APPID
	 */
	public static String BAIDU_ID = null;

	/**
	 * 百度翻译KEY
	 */
	public static String BAIDU_KEY = null;



	/**
	 * TOPT双方私有密钥
	 */
	public static String TOPT_KEY = null;
	
	/**
	 * SSL密码
	 */
	public static String SSL_PASSWD = null;




    public static void laodConfig() {
		// [服务器]
		SERVER_ID 												= loadstring("Server.ID");
		SERVER_LANGUAGE 										= loadstring("Server.Language");

		// [DB]
		SERVER_MARIADB 											= loadboolean("DB.Mariadb");
		if (SERVER_MARIADB) {
			DB_IP 												= loadstring("DB_Mariadb.IP");
			DB_PORT 											= loadstring("DB_Mariadb.Port");
			DB_NAME 											= loadstring("DB_Mariadb.Name");
			DB_USER 											= loadstring("DB_Mariadb.User");
			DB_PASSWD 											= loadstring("DB_Mariadb.Passwd");
		}

		// [登录]
		LOGIN_TIME 												= loadint("Login_Time");
		LOGIN_RADICAL 											= loadboolean("Login_Radical");
		PERMISSION_PASSING 										= loadboolean("Permission_Passing");
		
		// [投票]
		VOTE_ADMIN 												= loadboolean("Vote.Admin");
		VOTE_NEW_PLAYER 										= loadboolean("Vote.New_Player");
		if (VOTE_NEW_PLAYER) {
            VOTE_NEW_PLAYER_TIME 								= loadint("Vote.New_Player.Time")*60;
        }

		// [昼夜变换]
		DAY_AND_NIGHT 											= loadboolean("Day_and_night");
		if (DAY_AND_NIGHT) {
			DAY_TIME 											= loadint("Day.Time");
			NIGHT_TIME 											= dis(loadint("Night.Time"));
		}

		// [建造限制]
		BUILDING_RESTRICTION 									= loadboolean("Building_Restriction");
		if (BUILDING_RESTRICTION) {
			BUILDING_WARNING_QUANTITY 							= loadint("Building_Wan_Construction");
			BUILDING_REJECT_QUANTITY 							= loadint("Building_Max_Construction");
		}

		// [单位限制]
		SOLDIER_RESTRICTION 									= loadboolean("Soldier_Restriction");
		if (SOLDIER_RESTRICTION) {
			SOLDIER_WARNING_QUANTITY 							= loadint("Soldier_Wan_Construction");
			SOLDIER_REJECT_QUANTITY 							= loadint("Soldier_Max_Construction");
		}

		// [邮件]
		MAIL_USE 												= loadboolean("Mail_Use");
		if (MAIL_USE) {
			MAIL_SMTP_IP 										= loadstring("Mail_SMTP.IP");
			MAIL_SMTP_PORT 										= loadstring("Mail_SMTP.Port");
			MAIL_SMTP_USER 										= loadstring("Mail_SMTP.User");
			MAIL_SMTP_PASSWD 									= loadstring("Mail_SMTP.Passwd");
			REGULAR_REPORTING 									= loadboolean("Mail_SMTP.Regular_Reporting");
			if (REGULAR_REPORTING) {
				REGULAR_REPORTING_TIME 							= loadint("Mail_SMTP.Regular_Reporting.Time");
				REGULAR_REPORTING_TOMAIL 						= loadstring("Mail_SMTP.Regular_Reporting.ToMail");
			}
		}
		// [显示]
		MAXIMUM_SCREEN_DISPLAY 									= loadint("Maximum_Screen_Display");

		// [Help]
		HELP_SHOW_UNAUTHORIZE_CONTENT 							= loadboolean("Help.Show_unauthorize_content");

		// [百度翻译]
		BAIDU_TR 												= loadboolean("Baidu_Tr");
		if (BAIDU_TR) {
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
