package extension.data.global;

import extension.core.ex.Vote;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class Data {

	public static final String Plugin_Path 						= "/config/mods";
	public static final String Plugin_Data_Path 				= "/config/mods/GA";
	public static final String Plugin_Lib_Path 					= "/config/mods/GA/lib";
	public static final String Plugin_Log_Path 					= "/config/mods/GA/log";
	public static final String Plugin_Resources_Path 			= "/config/mods/GA/resources";
	public static final String Plugin_Resources_bundles_Path 	= "/config/mods/GA/resources/bundles";
	public static final String Plugin_Resources_Other_Path 		= "/config/mods/GA/resources/other";

    public static final Charset UTF8 = StandardCharsets.UTF_8;

	// [线程]

	public static final ScheduledExecutorService service 		= Executors.newScheduledThreadPool(5);
	public static final ThreadPoolExecutor Thred_service 		= new ThreadPoolExecutor(5,15,1,TimeUnit.MINUTES,new LinkedBlockingDeque<Runnable>(10));
	public static final ThreadPoolExecutor Thred_DB_service 	= new ThreadPoolExecutor(4,4,1,TimeUnit.MINUTES,new LinkedBlockingDeque<Runnable>());
	//public static final ExecutorService Thred_Async_service 	= new ThreadPoolExecutor(5,5,1,TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>());	

	// [Cache]

	public static Vote vote;
	public static boolean ismsg = true;

}
