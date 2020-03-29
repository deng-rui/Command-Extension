package extension.core;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
//Java 

import extension.data.db.PlayerData;
import extension.data.global.Maps;
import extension.util.Log;
//GA-Exted

import static extension.util.DateUtil.getLocalTimeFromUTC;
//Static

public class Threads {

	private static ScheduledFuture Thread_Time;
	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

	static {
		Runnable Atime=new Runnable() {
			@Override
			public void run() {
				LoginStatus();
			}
		};
		Thread_Time=service.scheduleAtFixedRate(Atime,0,30,TimeUnit.MINUTES);
	}

	public static void close() {
		Thread_Time.cancel(true);
		service.shutdown();
	}

	// 用户过期?
	public static void LoginStatus() {
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
}