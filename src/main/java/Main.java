package extension;

import java.io.*;

import arc.util.CommandHandler;
//Arc

import mindustry.plugin.Plugin;
//Mindustry

import extension.core.ClientCommandsx;
import extension.core.Event;
import extension.core.Initialization;
import extension.core.ServerCommandsx;
import extension.util.log.Log;
//GA-Exted

import extension.util.file.FileUtil;
import extension.net.server.Start;
import extension.core.ex.Threads;


public class Main extends Plugin {
	//动态难度
	//PVP限制

	public Main() {
		
		// Log 权限
		Log.Set("ALL");

		// 初始化 所需依赖
		new Initialization().Start_Initialization();

		// 加载Event
		new Event().register();

		// 启动WEB服务
		Threads.NewThred_SE(() -> new Start());

/*
		翻译测试
		Log.info("Bing",new Bing().translate("必应翻译","en"));
		Log.info("Baidu",new Baidu().translate("百度翻译","en"));
		Log.info("Google",new Google().translate("谷歌翻译","en"));
*/
		// 内网玩家 Ngrok
		Threads.NewThred_SE(() -> {
			String line;
		    Process p;
		    BufferedReader input = null;
		    try {
		        p = Runtime.getRuntime().exec(FileUtil.File("/").getPath()+"start.sh");
		        input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        while ((line = input.readLine()) != null) {
		            System.out.println(line);
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
