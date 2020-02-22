package extension.core;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
//Java

import mindustry.entities.type.Player;
//Mindustry

import static mindustry.Vars.playerGroup;
//Mindustry-Static

public class Vote {
	private static Player player;
	private static String type;
	private static String mapname;
	private static int require;
	private static int reciprocal;
	public static boolean sted=true;

	public Vote(Player player, String type, String mapname){
		this.player = player;
		this.type = type;
		this.mapname = mapname;
		start();
	}

	public Vote(String mapname){
		start();
	}

	void start(){
		ScheduledExecutorService service=Executors.newScheduledThreadPool(2);//两条线程
		if(!sted) {
			return;
		}
		if(playerGroup.size() == 1){
			//player.sendMessage();
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
				service.shutdown();
			}
		},50,TimeUnit.SECONDS);

		reciprocal=6;
		sted = false;

	}

	void end() {
		sted = true;

	}
}