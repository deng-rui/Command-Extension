package extension.core.ex;

import java.util.List;
//Java

import arc.struct.Array;
import arc.struct.Array.ArrayIterable;
import arc.util.Timer;
import arc.util.Timer.*;
//Arc

import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.maps.MapException;
import mindustry.entities.type.Player;
//Mindustry

import static mindustry.Vars.logic;
import static mindustry.Vars.state;
import static mindustry.Vars.world;
import static mindustry.Vars.net;
import static mindustry.Vars.netServer;
import static mindustry.Vars.playerGroup;
//Mindustry-Static

import extension.core.Event;
import extension.data.global.Config;
import extension.data.global.Maps;
import extension.data.db.PlayerData;
import extension.util.log.Log;
//GA-Exted

import static extension.util.ExtractUtil.longToIP;
import static extension.util.DateUtil.simp;
//Static

public class Extend {
	public static boolean Authority_control(Player player, String a) {
		// 未开启登录 直接抢答结果
		// 逻辑有问题:(
		//if(!Config.Login) return true;
		return (boolean)Maps.getPower_Data((int)Maps.getPlayer_Data(player.uuid).Authority).contains(a);
	}

	public static void loadmaps(boolean wait, Runnable run, Gamemode mode){
		Runnable r = () -> {
			Array<Player> players = new Array<>();
			Array<Player> playerspvp = new Array<>();
			for(Player p : playerGroup.all()){
				if(p.con == null) continue;
				players.add(p);
				if(Maps.getPlayer_Data(p.uuid).Authority > 0)
					playerspvp.add(p);
				p.setDead(true);
			}		
			logic.reset();
			Call.onWorldDataBegin();
			run.run();
			state.rules = world.getMap().applyRules(mode);
			logic.play();
			for(Player p : players){
				p.reset();
				if(Maps.getPlayer_Data(p.uuid).Authority > 0) {
					if(state.rules.pvp) 
						p.setTeam(netServer.assignTeam(p, new ArrayIterable<>(playerspvp)));
				} else {
					p.setTeam(Team.derelict);
				}
				netServer.sendWorldData(p);
			}
		};

		if(wait){
			final Task lastTask = new Task(){
				public void run(){
					try{
						r.run();
					}catch(MapException e){
						Log.warn(e.map.name() + ": " + e.getMessage());
						net.closeServer();
					}
				}
			};
			// 延迟4S. 微调
			Timer.schedule(lastTask, 4);
		}else{
			r.run();
		}

		Event.syncTeam();
	}

	public static Object[] PlayerdatatoObject(PlayerData d){
		Object[] params = {d.NAME,d.UUID,longToIP(d.IP),d.Country,d.Language,	d.Level,d.Exp,d.Reqexp,d.Reqtotalexp,d.Buildcount,d.Cumulative_build,d.Pipe_build,d.Dismantledcount,d.Pvpwincount,d.Pvplosecount,d.Authority,secToTime(d.Playtime),simp(d.LastLogin*1000L,d.Time_format),simp(d.Lastchat*1000L,d.Time_format),d.Killcount,d.Deadcount,d.Joincount,d.Breakcount};
		return params;
	}

	public static String secToTime(long time) {
        String timeStr = null;
        long hour = 0;
        long minute = 0;
        long second = 0;
        if(time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            hour = minute / 60;
            minute = minute % 60;
            second = time - hour * 3600 - minute * 60;
            timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
        }
        return timeStr;
    }
 
    private static String unitFormat(long i) {
        String retStr = null;
        if(i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }

    public static void addMesg_Team(String msg, Team team){
    	for (Player player : playerGroup.all())
			if(player.getTeam().equals(team))
				player.sendMessage(msg);
    }

    public static void addMesg_Admin(String msg){
    	for (Player player : playerGroup.all())
			if(player.isAdmin)
				player.sendMessage(msg);
    }
}