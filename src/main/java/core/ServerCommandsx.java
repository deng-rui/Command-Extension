package extension.core;

import arc.Core;
import arc.Events;
import arc.util.CommandHandler;
//Arc

import mindustry.game.Team;
import mindustry.game.EventType.GameOverEvent;
//Mindustry

import static arc.util.Log.info;
import static mindustry.Vars.net;
import static mindustry.Vars.maps;
//Mindustry-Static

import extension.data.db.PlayerData;
import extension.data.global.Maps;
//

import extension.core.Initialization;
import extension.core.ex.Threads;
//Static

public class ServerCommandsx {
	public void register(CommandHandler handler) {
		handler.removeCommand("exit");
		handler.removeCommand("gameover");
		handler.removeCommand("reloadmaps");

		handler.register("testinfo","[GA]" ,"GA TEST", (arg) -> {
			//info("{0}", getinput(arg[0]));
		System.out.println((arg.length > 0) ? arg[0] : null);
	
		});

		handler.register("reloadmaps", "reload maps", (arg) -> {
			int beforeMaps = maps.all().size;
			maps.reload();
			if(maps.all().size > beforeMaps){
				info("&lc{0}&ly new map(s) found and reloaded.", maps.all().size - beforeMaps);
			}else{
				info("&lyMaps reloaded.");
			}
			new Initialization().MapList();
		});

		handler.register("gameover", "Force a game over", (arg)-> {
			info("&lyCore destroyed.");
			Events.fire(new GameOverEvent(Team.crux));
		});

		handler.register("reloadconfig", "reload Command-Extension Config.ini", (arg)-> {
			info("&lyReLoad Config.ini End.");
			new Initialization().ReLoadConfig();
		});

		handler.register("toadmin", "<uuid> <id>","reload Command-Extension Config.ini", (arg)-> {
			PlayerData playerdata = Maps.getPlayer_Data(arg[0]);
			if (playerdata != null)
				playerdata.Authority = Integer.parseInt(arg[1]);
		});

		handler.register("exit", "Exit the server application", (arg)-> {
			info("Shutting down server.");
			net.dispose();
			Threads.close();
			Core.app.exit();
		});
	}
}