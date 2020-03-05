package extension.core;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;
//Java

import mindustry.net.Packets.KickReason;
import mindustry.gen.Call;
import mindustry.entities.type.Player;
import mindustry.game.Gamemode;
//Mindustry

import static mindustry.Vars.logic;
import static mindustry.Vars.maps;
import static mindustry.Vars.playerGroup;
import static mindustry.Vars.world;
//Mindustry-Static

import static extension.util.LocaleUtil.getinput;
//Static

public class Vote {
	private static Player player;
	private static String type;
	private static String name;
	private static int require;
	private static int reciprocal;
	public static boolean sted=true;
	static List<String> playerlist = new ArrayList<String>();

	public Vote(Player player, String type, String name){
		this.player = player;
		this.type = type;
		this.name = name;
		start();
	}

	public Vote(Player player, String type){
		this.player = player;
		this.type = type;
		start();
	}

	public Vote(){
		start();
	}

	private void start(){
		ScheduledExecutorService service=Executors.newScheduledThreadPool(2);//两条线程
		if(!sted) {
			return;
		}
		if(playerGroup.size() == 1){
			//player.sendMessage();
			require = 1;
		} else if(playerGroup.size() <= 3){
			require = 2;
		} else {
			require = (int) Math.ceil((double) playerGroup.size() / 2);
		}
		
		Runnable Countdown=new Runnable() {
			@Override
			public void run() {
				reciprocal--;
			}
		};
		//倒计时 10S/r
		ScheduledFuture Count_down=service.scheduleAtFixedRate(Countdown,10,10,TimeUnit.SECONDS);

		service.schedule(new Runnable() {
			@Override
			public void run() {
				Count_down.cancel(true);
				end();
				sted = true;

				service.shutdown();
			}
		},58,TimeUnit.SECONDS);

		reciprocal=6;
		sted = false;

	}

	private void end() {
		if (playerlist.size() >= require) {
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
				default :
					defaulta();
					return;

			}
		} else {
			Call.sendMessage(getinput("vote.done.no",name));
		}
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
	private void host() {/*
		Map result = maps.all().find(map -> map.name().equalsIgnoreCase(mapp.replace('_', ' ')) || map.name().equalsIgnoreCase(mapp));
		Gamemode preset = Gamemode.survival;
		try{
			preset = Gamemode.valueOf(gamemodes);
		}catch(IllegalArgumentException e){
			return;
		}
		logic.reset();
		world.loadMap(result,result.applyRules(preset));*/
	}
	private void skipwave() {
		for (int i = 0; i < 10; i++) {
			logic.runWave();
		}
	}
	private void defaulta() {
		Call.sendMessage(getinput("vote.end.err",type));
	}
}