package extension.core.ex;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
//Java 

import arc.Core;
import arc.util.Time;
//

import mindustry.gen.Call;
import mindustry.core.GameState.State;
//

import static mindustry.Vars.state;
//

import extension.data.db.PlayerData;
import extension.data.global.Config;
import extension.data.global.Data;
import extension.data.global.Maps;
import extension.util.log.Log;
//GA-Exted

import static extension.core.ex.Extend.secToTime;
import static extension.util.file.LoadConfig.CustomLoad;
import static extension.util.DateUtil.getLocalTimeFromUTC;
//Static


//import javax.mail.NoSuchProviderException;
//import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.Message;
//import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.Address;
import javax.activation.*;
//
import com.sun.mail.util.MailSSLSocketFactory;
//

public class Threads {

	private static ScheduledFuture Thread_Time;
	private static int status_login = 1;
	private static int status_mail = 1;
	private static int status_day_night = 1;
	// 夜晚渐变 => Gradual change at night
	private static float current_time = 0f;
	private static boolean day_or_night = true;
	private static boolean gradual_change = true;

	// 定时任务 1min/S
	static {
		Runnable Atime=new Runnable() {
			@Override
			public void run() {

				Custom();
				Data.ismsg = true;
				//
				if (Config.Login)
					LoginStatus();
				if (Config.Day_and_night)
					Day_and_night_shift();
				if (Config.Regular_Reporting)
					Status_Reporting();
			}
		};
		Thread_Time=Data.service.scheduleAtFixedRate(Atime,1,1,TimeUnit.MINUTES);
	}

	public static void close() {
		Thread_Time.cancel(true);
		Data.service.shutdown();
		Data.Thred_service.shutdown();
		Data.Thred_DB_service.shutdown();
	}

	public static void NewThred_DB(Runnable run) {
		Data.Thred_DB_service.execute(run);
	}

	public static void NewThred_SE(Runnable run) {
		Data.Thred_service.execute(run);
	}

	// 用户过期?
	private static void LoginStatus() {
		if (Config.Login_Time > status_login) {
			status_login++;
			return;
		}
		status_login = 1;
		Map data = Maps.getMapPlayer_Data();
		Iterator it = data.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (Entry)it.next();
			PlayerData playerdata = (PlayerData)entry.getValue();
			long currenttime = getLocalTimeFromUTC()-0;
			// 防止初期登录就被刷
			if (playerdata.Backtime != 0)
				if (playerdata.Backtime <= currenttime) 
					Maps.removePlayer_Data((String)entry.getKey());
		}
	}

	private static void Status_Reporting() {
		if (Config.Regular_Reporting_Time > status_mail) {
			status_mail++;
			return;
		}
		status_mail = 1;
		try {
			Properties props = new Properties();
			props.setProperty("mail.smtp.auth", "true");  
			props.setProperty("mail.host", Config.Mail_SMTP_IP);
			props.setProperty("mail.smtp.port", Config.Mail_SMTP_Port);
			props.setProperty("mail.transport.protocol", "smtp");
			MailSSLSocketFactory sf = new MailSSLSocketFactory();
			sf.setTrustAllHosts(true);
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.ssl.socketFactory", sf);
			Session session = Session.getInstance(props);
			Message msg = new MimeMessage(session);
			msg.setSubject("Server current status");
			//
			OperatingSystemMXBean system = ManagementFactory.getOperatingSystemMXBean();
			final long MB = 1024 * 1024;
			long totalPhysicalMemory = getLongFromOperatingSystem(system,"getTotalPhysicalMemorySize");
			long freePhysicalMemory = getLongFromOperatingSystem(system, "getFreePhysicalMemorySize");
			long usedPhysicalMemorySize =totalPhysicalMemory - freePhysicalMemory;
			Object[] pasm = {system.getName(),totalPhysicalMemory/MB,freePhysicalMemory/MB,usedPhysicalMemorySize/MB,Core.graphics.getFramesPerSecond(),Core.app.getJavaHeap()/MB,secToTime((long)ManagementFactory.getRuntimeMXBean().getUptime()/1000),getLocalTimeFromUTC(0,1)+" UTC"};
			//
			msg.setContent(CustomLoad("Mail.Report",pasm),"text/html;charset = UTF-8");
			msg.setFrom(new InternetAddress(Config.Mail_SMTP_User));
			Transport transport = session.getTransport();
			transport.connect(Config.Mail_SMTP_IP,Config.Mail_SMTP_User,Config.Mail_SMTP_Passwd);
			transport.sendMessage(msg, new Address[] { new InternetAddress(Config.Regular_Reporting_ToMail)});
			transport.close();
		} catch (Exception e) {
			// 暂时不捕获
		}
	}

	private static long getLongFromOperatingSystem(OperatingSystemMXBean operatingSystem, String methodName) {
		try {
			final Method method = operatingSystem.getClass().getMethod(methodName,
					(Class<?>[]) null);
			method.setAccessible(true);
			return (Long) method.invoke(operatingSystem, (Object[]) null);
		} catch (final InvocationTargetException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			}
			throw new IllegalStateException(e.getCause());
		} catch (final NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		} catch (final IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void Day_and_night_shift() {
		// 白天黑夜
		if(day_or_night) {
			// 渐变
			if(gradual_change) {
				// 前半夜
				if(current_time < 1.0f)
					current_time = current_time + Config.Night_Time;
				else{
					// 后半夜
					gradual_change = false; 
					current_time = current_time - Config.Night_Time;
				}
			} else if(current_time >= 0f) 
					current_time = current_time - Config.Night_Time;
				else {
					// 转白天
					day_or_night = false;
					status_day_night++;
				}    
		}else{
			// 白天的0-1.5f好像没有变换
			if (status_day_night < Config.Day_Time)
				status_day_night++;
			else{
				day_or_night = true;
				gradual_change = true;
				status_day_night = 1;
				current_time = current_time - Config.Night_Time;  
			}
		}
		// 夜晚设置
		if (current_time > 0) {
			state.rules.lighting = true;
			state.rules.ambientLight.a = current_time;
		} else
			state.rules.lighting = false;
		// 设置规则
		Call.onSetRules(state.rules);
	}

	private static void Custom() {
		state.rules.playerDamageMultiplier = 0f;
		Call.onSetRules(state.rules);
	}
}