package com.github.dr.extension.core.ex;

import arc.struct.Array;
import arc.struct.Array.ArrayIterable;
import arc.util.Timer;
import arc.util.Timer.Task;
import com.github.dr.extension.core.Event;
import com.github.dr.extension.data.db.PlayerData;
import com.github.dr.extension.data.global.Maps;
import com.github.dr.extension.util.log.Log;
import mindustry.entities.type.Player;
import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.maps.MapException;

import static com.github.dr.extension.util.DateUtil.simp;
import static com.github.dr.extension.util.ExtractUtil.longToIp;
import static mindustry.Vars.*;

public class Extend {

    public static boolean authorityControl(Player player, String a) {
		// 未开启登录 直接抢答结果
		// 逻辑有问题:(
		//if(!Config.Login) return true;
		return (boolean)Maps.getPowerData((int)Maps.getPlayerData(player.uuid).authority).contains(a);
	}

	public static void loadmaps(boolean wait, Runnable run, Gamemode mode){
		Runnable r = () -> {
			Array<Player> players = new Array<>();
			Array<Player> playerspvp = new Array<>();
			for(Player p : playerGroup.all()){
				if(p.con == null) {
                    continue;
                }
				players.add(p);
				if(0 < Maps.getPlayerData(p.uuid).authority) {
                    playerspvp.add(p);
                }
				p.setDead(true);
			}		
			logic.reset();
			Call.onWorldDataBegin();
			run.run();
			state.rules = world.getMap().applyRules(mode);
			logic.play();
			for(Player p : players){
				p.reset();
				if(Maps.getPlayerData(p.uuid).authority > 0) {
					if(state.rules.pvp) {
                        p.setTeam(netServer.assignTeam(p, new ArrayIterable<>(playerspvp)));
                    }
				} else {
					p.setTeam(Team.derelict);
				}
				netServer.sendWorldData(p);
			}
		};

		if(wait){
			final Task lastTask = new Task(){
				@Override
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


    public static Object[] playerdatatoObject(PlayerData d){
		Object[] params = {d.name,d.uuid,longToIp(d.ip),d.country,d.language,d.level,d.exp,d.reqexp,d.reqtotalExp,d.buildCount,d.cumulativeBuild,d.pipeBuild,d.dismantledCount,d.pvpwinCount,d.pvploseCount,d.authority,simp(d.authorityEffectiveTime*1000L,d.timeFormat),secToTime(d.playTime),simp(d.lastLogin*1000L,d.timeFormat),simp(d.lastChat*1000L,d.timeFormat),d.killCount,d.deadCount,d.joinCount,d.breakCount};
		return params;
	}

	public static String secToTime(long time) {
        String timeStr = null;
        long hour = 0;
        long minute = 0;
        long second = 0;
        if(time <= 0) {
            return "00:00";
        } else {
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
        if(i >= 0 && i < 10) {
            retStr = "0" + i;
        } else {
            retStr = "" + i;
        }
        return retStr;
    }

    public static void addMesgTeam(Team team, String msg, Object...params){
    	for (Player player : playerGroup.all()) {
            if(player.getTeam().equals(team)) {
                player.sendMessage(Maps.getPlayerData(player.uuid).info.getinput(msg, params));
            }
        }
    }

    public static void addMesgAdmin(String msg, Object...params){
    	for (Player player : playerGroup.all()) {
            if(player.isAdmin) {
                player.sendMessage(Maps.getPlayerData(player.uuid).info.getinput(msg, params));
            }
        }
    }

    public static void addMesgAll(String msg, Object...params){
    	for (Player player : playerGroup.all()) {
            player.sendMessage(Maps.getPlayerData(player.uuid).info.getinput(msg,params));
        }
    }
}