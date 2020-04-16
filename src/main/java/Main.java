package extension;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.util.CommandHandler;
//Arc

import mindustry.plugin.Plugin;
//Mindustry

import extension.core.ClientCommandsx;
import extension.core.Event;
import extension.core.Initialization;
import extension.core.ServerCommandsx;
import extension.core.ex.Threads;
import extension.util.Log;
//GA-Exted



import java.lang.reflect.Field;
import arc.util.CommandHandler.Command;
import arc.struct.Array;
import arc.struct.ObjectMap;

import java.io.*;
import java.util.*;
import extension.util.file.FileUtil;


public class Main extends Plugin {
	//动态难度
	//PVP限制
	private boolean a = true;

	public Main() {
		
		//Log 权限
		Log.Set("ALL");

		//初始化 所需依赖
		new Initialization().Start_Initialization();

		//加载Event
		new Event().register();
/*
NGROK-OK
		Threads.NewThred_SE(() -> {
			String line;
		    Process p;
		    BufferedReader input = null;

		    try {
		        p = Runtime.getRuntime().exec(FileUtil.File("/").getPath()+"ngrok -log="+FileUtil.File("/").getPath()+"ngrok.log -config ngrok.conf start httptun tcptun");

		        input = new BufferedReader(
		                new InputStreamReader(p.getInputStream()));

		        if(a) {
		        	while ((line = input.readLine()) != null) {
		            System.out.println(line);
		        }
		        a = false;
		        }
		        
		    } catch (IOException e) {
		        e.printStackTrace();
		    } finally {
		        if (null != input) {
		            try {
		                input.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
		       }
		});
*/
	}

	@Override
	public void init(){
		new Initialization().Override_Initialization();
		// 部分加载需要服务器加载完毕 例如maps
		new Initialization().MapList();
	}	

	@Override
	public void registerServerCommands(CommandHandler handler){
		new ServerCommandsx().register(handler);
	};

	@Override
	public void registerClientCommands(CommandHandler handler) {
		new ClientCommandsx().register(handler);
	}
/*
	// 2020.4.9 13:30
	// 通过反射删除命令list 即隐藏命令
	try {
			CommandHandler dd2 = Vars.netServer.clientCommands;
		Field field = dd2.getClass().getDeclaredField("orderedCommands");
		//Field bbb = dd2.getClass().getDeclaredField("commands");
		//bbb.setAccessible(true);
		field.setAccessible(true);

        //Command cmd = new Command("bbb", "", "description", (args, p) -> info("yes!!!."));
        Array<Command> aaa = Array.withArrays(field.get(dd2));
		//ObjectMap<String, Command> aa = ObjectMap.of(bbb.get(dd2));

        //aa.put("bbb".toLowerCase(), cmd);
        aaa.remove(dr);
		//field.set(handler, aaa);      
		field.set(dd2, aaa);      
	}catch(Exception ex){
			Log.fatal("Cover Gameover error",ex);
	} 
*/	  

}

/*2020/1/4 10:64:33
 *本项目使用算法
 *名称								使用算法	  			来源
 *UTF8Control.Java					UTF8Control  		https://answer-id.com/52120414
 *GoogletranslateApi.Java			Googletranslate		https://github.com/PopsiCola/GoogleTranslate
 *Event.Java 						assigner 			Tencent qun(QQ qun) - linglan
 *BadWord.Java 						参考DFA算法 			http://blog.csdn.net/chenssy/article/details/26961957
*/
