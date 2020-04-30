package extension.data.global;

import extension.core.ex.Vote;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public final class Data {

	public static final String PLUGIN_PATH 						= "/config/mods";
	public static final String PLUGIN_DATA_PATH 				= "/config/mods/GA";
	public static final String PLUGIN_LIB_PATH 					= "/config/mods/GA/lib";
	public static final String PLUGIN_LOG_PATH 					= "/config/mods/GA/log";
	public static final String PLUGIN_RESOURCES_PATH 			= "/config/mods/GA/resources";
	public static final String PLUGIN_RESOURCES_BUNDLES_PATH 	= "/config/mods/GA/resources/bundles";
	public static final String PLUGIN_RESOURCES_OTHER_PATH 		= "/config/mods/GA/resources/other";

    public static final Charset UTF8 = StandardCharsets.UTF_8;

	// [线程]

	public static final ScheduledExecutorService SERVICE 		= Executors.newScheduledThreadPool(5);
	public static final ThreadPoolExecutor THRED_SERVICE 		= new ThreadPoolExecutor(5,15,1,TimeUnit.MINUTES,new LinkedBlockingDeque<Runnable>(10));
	public static final ThreadPoolExecutor THRED_DB_SERVICE 	= new ThreadPoolExecutor(4,4,1,TimeUnit.MINUTES,new LinkedBlockingDeque<Runnable>());
	//public static final ExecutorService Thred_Async_service 	= new ThreadPoolExecutor(5,5,1,TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>());	

	// [Cache]

	public static Vote VOTE;
	public static boolean ISMSG = true;

}
