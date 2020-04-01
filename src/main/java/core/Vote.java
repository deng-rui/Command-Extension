package extension.core;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
//Java

import arc.Events;
import arc.math.Mathf;
import arc.util.Time;
//Arc

import mindustry.net.Packets.KickReason;
import mindustry.gen.Call;
import mindustry.game.Team;
import mindustry.entities.type.Player;
import mindustry.game.Gamemode;
import mindustry.game.EventType.GameOverEvent;
import mindustry.maps.Map;
//import mindustry.maps.Maps.*;
import mindustry.world.Tile;
import mindustry.Vars;
//Mindustry

import static mindustry.Vars.logic;
import static mindustry.Vars.maps;
import static mindustry.Vars.netServer;
import static mindustry.Vars.playerGroup;
import static mindustry.Vars.world;
import static mindustry.Vars.state;
//Mindustry-Static

import extension.data.global.Config;
import extension.util.Log;
//GA-Exted

import static extension.core.Extend.loadmaps;
import static extension.data.global.Lists.getMaps_List;
import static extension.util.DateUtil.getLocalTimeFromUTC;
import static extension.util.LocaleUtil.getinput;
import static extension.util.IsUtil.Blank;
//Static

public class Vote {
	private static Player player;
	private static String type;
	private static String name;
	private static int require;
	private static int reciprocal;
	public static Team team;
	public static boolean sted = true;
	public static boolean isteam = false;
	private static ScheduledFuture Vote_time;
	private static ScheduledFuture Count_down;
	public static List<String> playerlist = new ArrayList<String>();

	public Vote(Player player, String type, String name){
		this.player = player;
		this.type = type;
		this.name = name;
		start();
	}

	public Vote(Player player, String type, Team team){
		this.player = player;
		this.type = type;
		this.team = team;
		isteam = true;
		start();
	}

	public Vote(Player player, String type){
		this.player = player;
		this.type = type;
		start();
	}

	public Vote(){
		if (playerlist.size() >= require) {
			Count_down.cancel(true);
			Vote_time.cancel(true);
			end();
		}
	}

	private void start(){
		int number = 0;
		if (isteam) {
			for (Player it : Vars.playerGroup.all())
				if (it.getTeam().equals(team))
					number++;
		}
		else
			number = playerGroup.size();
		if(number == 1){
			player.sendMessage(getinput("vote.no1"));
			require = 1;
		} else if(number <= 3){
			require = 2;
		} else {
			require = (int) Math.ceil((double) number / 2);
		}
		Call.sendMessage(getinput("vote.start",type));
		Runnable Countdown=new Runnable() {
			@Override
			public void run() {
				reciprocal = reciprocal-10;
				Call.sendMessage(getinput("vote.ing",reciprocal));
			}
		};
		//倒计时 10S/r
		Count_down=Config.service.scheduleAtFixedRate(Countdown,10,10,TimeUnit.SECONDS);
		Runnable Votetime=new Runnable() {
			@Override
			public void run() {
				Count_down.cancel(true);
				end();
			}
		};
		//倒计时58s 便于停止
		Vote_time=Config.service.schedule(Votetime,58,TimeUnit.SECONDS);
		reciprocal=60;
		sted = false;

	}

	private void end() {
		//释放内存-
		Count_down=null;
		Vote_time=null;
		//-
		if ((Blank(playerlist.size())? 0:playerlist.size()) >= require) {
			Call.sendMessage(getinput("vote.ok"));
			playerlist.clear();
			switch(type){
				case "kick" :
					kick();
					break;
				case "host" :
					host();
					break;
				case "skipwave" :
					skipwave();
					break;
				case "gameover" :
					gameover();
					break;
				case "ff" :
					ff();
					break;
				default :
					defaulta();
					break;
			}
		} else {
			Call.sendMessage(getinput("vote.done.no",type,Blank(playerlist.size())? 0:playerlist.size(),playerGroup.size()));
		}
		isteam = false;
		sted = true;
	}

	private void kick() {
		Player target = playerGroup.find(p -> p.name.equals(name));
		if(target != null){
			Call.sendMessage(getinput("vote.kick.done",name));
			target.con.kick(KickReason.kick);
			return;
		}
		Call.sendMessage(getinput("vote.kick.err",target.name));
	}

	private void host() {
		List<String> MapsList = (List<String>)getMaps_List();
		String [] data = MapsList.get(Integer.parseInt(name)).split("\\s+");
		Map result = maps.all().find(map -> map.name().equalsIgnoreCase(data[0].replace('_', ' ')) || map.name().equalsIgnoreCase(data[0]));
		Gamemode mode = Gamemode.survival;
		try{
			mode = Gamemode.valueOf(data[2]);
		}catch(IllegalArgumentException ex){
			player.sendMessage(getinput("host.mode",data[2]));
			return;
		}
		final Gamemode gamemode = mode;
		Call.sendMessage(getinput("host.re"));
		loadmaps(true, () -> world.loadMap(result, result.applyRules(gamemode)),gamemode);
	}

	private void skipwave() {
		for (int i = 0; i < 10; i++) {
			logic.runWave();
		}
	}

	private void gameover() {
		Events.fire(new GameOverEvent(Team.crux));
	}

	private void ff() {
		for (Player it : Vars.playerGroup.all())
			if (it.getTeam().equals(team)) {
				killTiles(it.getTeam());
				it.kill();
				it.setTeam(Team.derelict);
			}

	}

	private void killTiles(Team team){
		for(int x = 0; x < world.width(); x++){
			for(int y = 0; y < world.height(); y++){
				Tile tile = world.tile(x, y);
				if(tile.entity != null && tile.getTeam() == team){
					Time.run(Mathf.random(60f * 6), tile.entity::kill);
				}
			}
		}
	}

	private void defaulta() {
		Call.sendMessage(getinput("vote.end.err",type));
	}
}