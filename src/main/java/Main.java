package extension;

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

import static java.lang.System.out;
import static io.anuke.mindustry.Vars.*;
import static io.anuke.mindustry.Vars.player;
import static extension.Extend.*;
import static extension.Tool.HttpRequest;
//Static

public class Main extends Plugin{

	Extend extend = new Extend ();
	Tool tool = new Tool ();

	@Override
	public void registerClientCommands(CommandHandler handler){

		handler.<Player>register("info","info me.", (args, player) -> {
			String ip = Vars.netServer.admins.getInfo(player.uuid).lastIP;
			String Country = tool.HttpRequest("http://ip-api.com/line/"+ip+"?fields=country");
			player.sendMessage("[green][INFO][] Acquisition...");
			try{
				Thread.currentThread().sleep(10000);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
			player.sendMessage("[green]Name[]: "+player.name);
			player.sendMessage("[green]UUID[]: "+player.uuid);
			player.sendMessage("[green]Equipment[]: "+player.isMobile);
			player.sendMessage("[green]IP[]: "+ip);
			player.sendMessage("[green]Country[]: "+Country);
		});

		handler.<Player>register("status","View server status", (args, player) -> {
			player.sendMessage("FPS:"+extend.status("getfps")+  "Occupied memory:"+extend.status("getmemory")+"MB");
			player.sendMessage("Online number:"+Vars.playerGroup.size());
			player.sendMessage("Total [scarlet]"+extend.status("getbancount")+"[] players banned.");
		});


		handler.<Player>register("getpos","View the current coordinates", (args, player) -> player.sendMessage("[green]The current coordinate is:[] X = " + Math.round(player.x/8) + "; Y = " + Math.round(player.y/8)));

		handler.<Player>register("tpp","<player> <player>","[red]Admin:[] Transfer to specified coordinates", (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage("[green]Careful:[] You're not admin!");
			} else {
				try{
					int x = Integer.parseInt(args[0])*8;
					int y = Integer.parseInt(args[1])*8;
					player.setNet((float)x, (float)y);
					player.set((float)x, (float)y);
				} catch (Exception e){
				player.sendMessage("[scarlet] XY command!");
				}
			}
		});

		handler.<Player>register("tp","<player...>","[red]Admin:[] Teleport to other players", (args, player) -> {
			Player other = Vars.playerGroup.find(p->p.name.equalsIgnoreCase(args[0]));
			if(!player.isAdmin){
				player.sendMessage("[green]Careful:[] You're not admin!");
			} else {
				if(other == null){
					player.sendMessage("[scarlet]playname command!");
					return;
				}
				player.setNet(other.x, other.y);
			}
		});

		handler.<Player>register("suicide","Kill yourself.", (args, player) -> {
				player.onPlayerDeath(player);
				Call.sendMessage(player.name+"[] [green]suicide[] command.");
		});

		handler.<Player>register("team","","[red]Admin:[] Replacement team", (args, player) ->{
			//change team
			if(!player.isAdmin){
				player.sendMessage("[green]Careful:[] You're not admin!");
				} else {
				if (!Vars.state.rules.pvp){
					player.sendMessage("Patterns are not PVP");
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

		handler.<Player>register("difficulty", "<difficulty>", "[red]Admin:[] Set server difficulty", (args, player) -> {
			if(!player.isAdmin){
				player.sendMessage("[green]Careful: [] You're not admin!");
			} else {
				try{
					Difficulty.valueOf(args[0]);
					player.sendMessage("Difficulty set to '"+args[0]+"'.");
				}catch(IllegalArgumentException e){
					player.sendMessage("No difficulty with name '"+args[0]+"' found.");
				}
			}
		});

		handler.<Player>register("gameover","","[red]Admin:[] End the game", (args, player) -> {
			
			if(!player.isAdmin){
				player.sendMessage("[green]Careful:[] You're not admin!");
			} else {
			/*
				netServer.kickAll(KickReason.gameover);
				state.set(State.menu);
				net.closeServer();
			*/
			}

		});


		handler.<Player>register("host","<mapname> [mode]","[red]Admin:[] ", (args, player) -> {
			
			if(!player.isAdmin){
				player.sendMessage("[green]Careful:[] You're not admin!");
			} else {
				String result=extend.host(args[0],args[1],"N");
				if (result != "Y") {
					player.sendMessage("Mode:"+args[1]+" is invalid!");
				}else{
					Call.sendMessage("[red]Restart after 10s, please re-enter![]");
					extend.host(args[0],args[1],"Y");
				}
			}
		});
		//It can be used normally. :)

		handler.<Player>register("runwave","","[red]Admin:[] Runwave.", (args, player) -> {
			
			if(!player.isAdmin){
				player.sendMessage("[green]Careful:[] You're not admin!");
			} else {
				logic.runWave();
			}
		});

		handler.<Player>register("time","View the current time of the server.", (args, player) -> player.sendMessage("[green]The current server time is[white]: "+extend.time()));

		handler.<Player>register("tr","<language> [Output-language]","test", (args, player) -> {
			/*
			player.sendMessage("zh-China ja-Japanese en-English ru-Russia");
			GoogleApi googleApi = new GoogleApi();
			try{
				Thread.currentThread().sleep(1000);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
			try{
				String result = googleApi.translate(args[0],args[1]);
				Call.sendMessage(player.name+"[green] say:[]: "+result);
				}catch(Exception e){
					return;
				}
				*/
			});

	}

}
