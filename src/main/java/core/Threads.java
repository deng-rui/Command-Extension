package extension.core;

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

import mindustry.core.GameState.State;
//

import static mindustry.Vars.state;
//

import extension.data.db.PlayerData;
import extension.data.global.Config;
import extension.data.global.Maps;
import extension.util.Log;
//GA-Exted

import static extension.core.Extend.secToTime;
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

	static {
		Runnable Atime=new Runnable() {
			@Override
			public void run() {
				if(Config.Login)
					LoginStatus();
				if(Config.Regular_Reporting)
					Status_Reporting();
			}
		};
		Thread_Time=Config.service.scheduleAtFixedRate(Atime,5,5,TimeUnit.MINUTES);
	}

	public static void close() {
		Thread_Time.cancel(true);
		Config.service.shutdown();
	}

	// 用户过期?
	public static void LoginStatus() {
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
			if(playerdata.Backtime <= currenttime) 
				Maps.removePlayer_Data((String)entry.getKey());
		}
	}

	public static void Status_Reporting() {
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
			Object[] pasm = {system.getName(),totalPhysicalMemory/MB,freePhysicalMemory/MB,usedPhysicalMemorySize/MB,Core.graphics.getFramesPerSecond(),Core.app.getJavaHeap()/MB,secToTime((long)ManagementFactory.getRuntimeMXBean().getUptime()/1000),getLocalTimeFromUTC(0,0)+" UTC"};
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
}