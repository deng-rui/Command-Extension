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

import static extension.util.DateUtil.getLocalTimeFromU;
import static extension.util.file.LoadConfig.customLoad;
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

	private static ScheduledFuture THREAD_TIME;
	private static int STATUS_LOGIN = 1;
	private static int STATUS_MAIL = 1;
	private static int STATUS_DAY_NIGHT = 1;
	//
	private static int STATUS_AUTHORITY = 1;
	// 夜晚渐变 => Gradual change at night
	private static float CURRENT_TIME = 0f;
	private static boolean DAY_OR_NIGHT = true;
	private static boolean GRADUAL_CHANGE = true;

	// 定时任务 1min/S

    public Threads() {
		Runnable Atime=new Runnable() {
			@Override
			public void run() {
				custom();
				Data.ismsg = true;
				//
				LoginStatus();
				AuthorityStatus();

				if (Config.DAY_AND_NIGHT) {
                    dayAndNightShift();
                }
				if (Config.Regular_Reporting) {
                    statusReporting();
                }
			}
		};
		THREAD_TIME=Data.SERVICE.scheduleAtFixedRate(Atime,1,1,TimeUnit.MINUTES);
	}

	public static void close() {
		THREAD_TIME.cancel(true);
		Data.SERVICE.shutdown();
		Data.THRED_SERVICE.shutdown();
		Data.THRED_DB_SERVICE.shutdown();
	}


    public static void NewThred_DB(Runnable run) {
		Data.THRED_DB_SERVICE.execute(run);
	}


    public static void NewThred_SE(Runnable run) {
		Data.THRED_SERVICE.execute(run);
	}

	// 用户过期?

    private static void LoginStatus() {
		if (Config.LOGIN_TIME > STATUS_LOGIN) {
			STATUS_LOGIN++;
			return;
		}
		STATUS_LOGIN = 1;
		Map data = Maps.getMapPlayer_Data();
		Iterator it = data.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (Entry)it.next();
			PlayerData playerdata = (PlayerData)entry.getValue();
			// 防止初期登录就被刷
			if (playerdata.backTime != 0L) {
				long currenttime = getLocalTimeFromU();
				if (playerdata.backTime <= currenttime) {
                    Maps.removePlayer_Data((String)entry.getKey());
                }
			}
		}
	}


    private static void AuthorityStatus() {
		if (5 > STATUS_AUTHORITY) {
			STATUS_AUTHORITY++;
			return;
		}
		STATUS_AUTHORITY = 1;
		Map data = Maps.getMapPlayer_Data();
		Iterator it = data.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (Entry)it.next();
			PlayerData playerdata = (PlayerData)entry.getValue();
			// 防止初期登录就被刷
			if (playerdata.authorityEffectiveTime != 0) {
				long currenttime = getLocalTimeFromU();
				if (playerdata.authorityEffectiveTime <= currenttime) {
					playerdata.authority = 1;
					playerdata.authorityEffectiveTime = 0;
				}
			}	
		}
	}


    private static void statusReporting() {
		if (Config.Regular_Reporting_Time > STATUS_MAIL) {
			STATUS_MAIL++;
			return;
		}
		STATUS_MAIL = 1;
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
			msg.setContent(customLoad("Mail.Report",pasm),"text/html;charset = UTF-8");
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


    private static void dayAndNightShift() {
		// 白天黑夜
		if(DAY_OR_NIGHT) {
			// 渐变
			if(GRADUAL_CHANGE) {
				// 前半夜
				if(CURRENT_TIME < 1.1f) {
                    CURRENT_TIME = CURRENT_TIME + Config.NIGHT_TIME;
                } else{
					// 后半夜
					GRADUAL_CHANGE = false;
					CURRENT_TIME = CURRENT_TIME - Config.NIGHT_TIME;
				}
			} else {
				if(CURRENT_TIME >= 0f) {
                    CURRENT_TIME = CURRENT_TIME - Config.NIGHT_TIME;
                } else {
					// 转白天
					DAY_OR_NIGHT = false;
					STATUS_DAY_NIGHT++;
				}  
			}	  
		} else {
			// 白天的0-1.5f好像没有变换
			if (status_day_night < Config.DAY_TIME) {
                status_day_night++;
            } else {
				DAY_OR_NIGHT = true;
				GRADUAL_CHANGE = true;
				STATUS_DAY_NIGHT = 1;
				CURRENT_TIME = CURRENT_TIME - Config.NIGHT_TIME;
			}
		}
		// 夜晚设置
		if (CURRENT_TIME > 0) {
			state.rules.lighting = true;
			state.rules.ambientLight.a = CURRENT_TIME;
		} else {
            state.rules.lighting = false;
        }
		// 设置规则
		Call.onSetRules(state.rules);
	}

	// 写死的定时任务

    private static void custom() {
		state.rules.playerDamageMultiplier = 0f;
		Call.onSetRules(state.rules);
	}
}