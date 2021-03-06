package com.github.dr.extension;

import arc.util.CommandHandler;
import com.github.dr.extension.core.ClientCommandsx;
import com.github.dr.extension.core.Event;
import com.github.dr.extension.core.Initialization;
import com.github.dr.extension.core.ServerCommandsx;
import com.github.dr.extension.core.ex.Threads;
import com.github.dr.extension.net.server.Start;
import com.github.dr.extension.util.log.Log;
import mindustry.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.github.dr.extension.util.file.FileUtil.File;


/**
 * @author Dr
 */
public class Main extends Plugin {

	public Main() {

		// Log 权限
		Log.set("ALL");

		// 初始化 所需依赖
		new Initialization().startInitialization();

		// 加载Event
		new Event().register();

		// 启动WEB服务
		Threads.newThredSe(() -> new Start());

		// 内网玩家 Ngrok
		Threads.newThredSe(() -> {
			String line;
			Process p;
			BufferedReader input = null;
			try {
				p = Runtime.getRuntime().exec(File("/").getPath() + "start.sh");
				input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while (true) {
					final String line1 = input.readLine();
					if ((line = line1) != null) {
						System.out.println(line);
					}
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
		new Initialization().overrideInitialization();
		// 部分加载需要服务器加载完毕 例如maps
		new Initialization().mapList();
	}	

	@Override
	public void registerServerCommands(CommandHandler handler){
		new ServerCommandsx().register(handler);
	};

	@Override
	public void registerClientCommands(CommandHandler handler) {
		new ClientCommandsx().register(handler);
	}
}

/*2020/1/4 10:64:33
 *本项目使用算法
 *名称								使用算法	  			来源
 *UTF8Control.Java					UTF8Control  		https://answer-id.com/52120414
 *GoogletranslateApi.Java			Googletranslate		https://github.com/PopsiCola/GoogleTranslate
 *Event.Java 						assigner 			Tencent qun(QQ qun) - linglan
 *BadWord.Java 						参考DFA算法 			http://blog.csdn.net/chenssy/article/details/26961957
*/
