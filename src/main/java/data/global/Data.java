package extension.data.global;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import extension.core.ex.Vote;

public class Data {

	public static final String Plugin_Path 						= "/config/mods";
	public static final String Plugin_Data_Path 				= "/config/mods/GA";
	public static final String Plugin_Lib_Path 					= "/config/mods/GA/lib";
	public static final String Plugin_Log_Path 					= "/config/mods/GA/log";
	public static final String Plugin_Resources_Path 			= "/config/mods/GA/resources";
	public static final String Plugin_Resources_bundles_Path 	= "/config/mods/GA/resources/bundles";
	public static final String Plugin_Resources_Other_Path 		= "/config/mods/GA/resources/other";

    public static final Charset UTF8 = Charset.forName("UTF-8");

	// [线程]
	public static final ScheduledExecutorService service 		= Executors.newScheduledThreadPool(5);
	//public static final ExecutorService executorService 		= Executors.newFixedThreadPool(10);
	// Core=10 MAX=15 Cache List=10
	public static final ThreadPoolExecutor Thred_service 		= new ThreadPoolExecutor(5,15,1,TimeUnit.MINUTES,new LinkedBlockingDeque<Runnable>(10));
	public static final ThreadPoolExecutor Thred_DB_service 	= new ThreadPoolExecutor(4,4,1,TimeUnit.MINUTES,new LinkedBlockingDeque<Runnable>());
	//public static final ExecutorService Thred_Async_service 	= new ThreadPoolExecutor(5,5,1,TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>());	

	// [Cache]
	public static Vote vote;
	public static boolean ismsg = true;

}
