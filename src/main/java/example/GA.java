package Dr;

import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.lang.Math;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//Java

import io.anuke.arc.*;
import io.anuke.arc.files.*;
import io.anuke.arc.util.*;
import io.anuke.arc.util.Timer;
import io.anuke.arc.util.CommandHandler.*;
import io.anuke.arc.util.Timer.*;
import io.anuke.arc.collection.*;
import io.anuke.arc.collection.Array.*;
//Arc

import io.anuke.mindustry.*;
import io.anuke.mindustry.core.*;
import io.anuke.mindustry.core.GameState.*;
import io.anuke.mindustry.content.*;
import io.anuke.mindustry.entities.*;
import io.anuke.mindustry.entities.type.*;
import io.anuke.mindustry.game.*;
import io.anuke.mindustry.game.Team;
import io.anuke.mindustry.game.Difficulty;
import io.anuke.mindustry.game.EventType;
import io.anuke.mindustry.game.EventType.PlayerJoin;
import io.anuke.mindustry.gen.*;
import io.anuke.mindustry.io.*;
import io.anuke.mindustry.maps.Map;
import io.anuke.mindustry.maps.*;
import io.anuke.mindustry.net.Administration.PlayerInfo ;
import io.anuke.mindustry.net.Packets.KickReason;
import io.anuke.mindustry.net.NetConnection;
import io.anuke.mindustry.net.Packets.KickReason ;
import io.anuke.mindustry.plugin.*;
import io.anuke.mindustry.plugin.Plugins.*;
import io.anuke.mindustry.type.*;
//Mindustry

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
//Jsoup

import static java.lang.System.out;
import static io.anuke.mindustry.Vars.*;
import static io.anuke.mindustry.Vars.player;
//Static

public class GA extends Plugin{

	//register commands that player can invoke in-game
	@Override
	public void registerClientCommands(CommandHandler handler){

		handler.<Player>register("info","info me.", (args, player) -> {
			// This command must be executed on a different thread.
			// Otherwise, the server will lag when this command is run.
			Thread thread1 = new Thread(new Runnable() {
				public void run(){
					try{
						String ip = Vars.netServer.admins.getInfo(player.uuid).lastIP;
						player.sendMessage("[green][INFO][] 获取中...");
						String connUrl = "http://ipapi.co/"+ip+"/country_name";
						Document doc = Jsoup.connect(connUrl).get();
						String geo = doc.text();
						player.sendMessage("[green]名字[]: "+player.name);
						player.sendMessage("[green]UUID[]: "+player.uuid);
						player.sendMessage("[green]设备[]: "+player.isMobile);
						player.sendMessage("[green]IP[]: "+ip);
						player.sendMessage("[green]国家[]: "+geo);
					} catch (Exception e){
						player.sendMessage("查询失败!");
					}
				}
			});
			thread1.start();
		});

		handler.<Player>register("status","查看服务器当前状态", (args, player) -> {
			float fps = Math.round((int)60f / Time.delta());
			float memory = Core.app.getJavaHeap() / 1024 / 1024;
			player.sendMessage("FPS:"+fps+  "占用内存:"+memory+"MB");
			player.sendMessage(Vars.playerGroup.size()+"个在线人数");
			int idb = 0;
			int ipb = 0;

			Array<PlayerInfo> bans = Vars.netServer.admins.getBanned();
			for(PlayerInfo info : bans){
				idb++;
			}

			Array<String> ipbans = Vars.netServer.admins.getBannedIPs();
			for(String string : ipbans){
				ipb++;
			}
			int bancount = idb + ipb;
			player.sendMessage(bancount+"个被禁止进入玩家");
		});


		handler.<Player>register("getpos","查看当前坐标", (args, player) -> {
			//player.sendMessage("X: " + Math.round(player.x) + " Y: " + Math.round(player.y));
			player.sendMessage("[green]当前坐标是:[] X = " + Math.round(player.x/8) + "; Y = " + Math.round(player.y/8));
		});

		handler.<Player>register("tpp","<player> <player>","[red]Admin:[] tp到指定XY", (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage("[green]Notice:[] You're not admin!");
			} else {
				try{
					int x = Integer.parseInt(args[0])*8;
					int y = Integer.parseInt(args[1])*8;
					//strict off
					//Call.onPositionSet(player.con.id, (float)x, (float)y);
					//strict on + local
					player.setNet((float)x, (float)y);
					player.set((float)x, (float)y);
				} catch (Exception e){
				player.sendMessage("[scarlet] XY无效!");
				}
			}
		});

		handler.<Player>register("tp","<player...>","[red]Admin:[] tp到指定玩家边", (args, player) -> {
			Player other = Vars.playerGroup.find(p->p.name.equalsIgnoreCase(args[0]));
			if(!player.isAdmin){
				player.sendMessage("[green]Notice:[] You're not admin!");
			} else {
				if(other == null){
					player.sendMessage("[scarlet]名称无效!");
					return;
				}
				player.setNet(other.x, other.y);
			}
		});

		handler.<Player>register("suicide","Kill yourself.", (args, player) -> {
				player.onPlayerDeath(player);
				Call.sendMessage(player.name+"[] [green]suicide[] command.");
		});

		handler.<Player>register("team","","[red]Admin:[] 更换队伍", (args, player) ->{
			//change team
			if(!player.isAdmin){
				player.sendMessage("[green]Notice:[] You're not admin!");
				} else {
				if (!Vars.state.rules.pvp){
					player.sendMessage("当前模式不是PVP");
					return;
				}
				int index = player.getTeam().ordinal()+1;
				while (index != player.getTeam().ordinal()){
					if (index >= Team.all.length){
						index = 0;
					}
					if (!Vars.state.teams.get(Team.all[index]).cores.isEmpty()){
						player.setTeam(Team.all[index]);
						break;
					}
					index++;
				}
				//kill player
				Call.onPlayerDeath(player);
			}
		});

		handler.<Player>register("difficulty","<difficulty>","[red]Admin:[] ", (args, player) -> {
			
			if(!player.isAdmin){
				player.sendMessage("[green]Notice:[] You're not admin!");
			} else {
				try{
					Difficulty.valueOf(args[0]);
					player.sendMessage("难度已更改为:"+args[0]+"'.");
				}catch(IllegalArgumentException e){
					player.sendMessage("难度名称:"+args[0]+"无效!");
				}
			}
		});

		handler.<Player>register("gameover","","[red]Admin:[] 结束游戏", (args, player) -> {
			
			if(!player.isAdmin){
				player.sendMessage("[green]Notice:[] You're not admin!");
			} else {
			/*
                netServer.kickAll(KickReason.gameover);
                state.set(State.menu);
                net.closeServer();
            */
			}

		});

/*
		handler.<Player>register("host","<mapname> [mode]","[red]Admin:[] ½áÊøÓÎÏ·", (args, player) -> {
			
			if(!player.isAdmin){
				player.sendMessage("[green]Notice:[] You're not admin!");
			} else {
				if("sandbox".equalsIgnoreCase(args[1])){
				}else if ("pvp".equalsIgnoreCase(args[1])){
				}else if ("accatk".equalsIgnoreCase(args[1])){
				}else if ("survival".equalsIgnoreCase(args[1])){
				}else{
					player.sendMessage("模式名称:"+args[1]+"无效!");
					return;
				}
				Call.sendMessage("[red]十秒后更换地图，请重新进入![]");

				try{
					Thread.currentThread().sleep(5000);
					}catch(InterruptedException ie){
					  ie.printStackTrace();
					}

                netServer.kickAll(KickReason.gameover);
                state.set(State.menu);
                net.closeServer();
                
				//
				//stop games
					Map result = maps.all().find(map -> map.name().equalsIgnoreCase(args[0].replace('_', ' ')) || map.name().equalsIgnoreCase(args[0]));
					if(result == null){
						//
						return;
					}
				Gamemode preset = Gamemode.survival;
				try{
					preset = Gamemode.valueOf(args[1]);
					}catch(IllegalArgumentException e){
						return;
					}
				
				logic.reset();
				world.loadMap(result,  result.applyRules(preset));
				state.rules = result.applyRules(preset);
				logic.play();
					try{
            net.host(Core.settings.getInt("port"));
        }catch(BindException e){
            state.set(State.menu);
        }catch(IOException e){
            state.set(State.menu);
        }
			}
			
		});
		//It can be used normally. :)
		*/

		handler.<Player>register("runwave","","[red]Admin:[] runwave.", (args, player) -> {
			
			if(!player.isAdmin){
				player.sendMessage("[green]Notice:[] You're not admin!");
			} else {
				logic.runWave();
			}
		});

		handler.<Player>register("time","View the current time of the server.", (args, player) -> {
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yy-M-d a h:m.ss");
			String nowString = now.format(dateTimeFormatter);
			player.sendMessage("[green]The current server time is[white]: "+nowString);
		});

		handler.<Player>register("tr","<language> [Code name]","test", (args, player) -> {
			player.sendMessage("zh-China ja-Japanese en-English ru-Russia");
//NOT-sleep
        	Call.sendMessage(result);
        	});

	}

}
