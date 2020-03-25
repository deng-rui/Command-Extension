package extension.core;

import java.util.List;
import java.util.Set;
import java.util.TimeZone;
//Java

import arc.Events;
//Arc

import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.entities.type.Player;
import mindustry.gen.Call;
import mindustry.game.EventType.GameOverEvent;
import mindustry.game.EventType.ServerLoadEvent;
import mindustry.game.EventType.PlayerChatEvent;
import mindustry.game.EventType.PlayerJoin;
import mindustry.game.EventType.PlayerLeave;
import mindustry.game.EventType.UnitDestroyEvent;
import mindustry.net.Administration.PlayerInfo;
import mindustry.net.Packets.KickReason;
import mindustry.net.NetConnection;
import mindustry.Vars;
//Mindustry

import static mindustry.Vars.state;
import static mindustry.Vars.netServer;
import static mindustry.Vars.playerGroup;
//Mindustry-Static

import extension.data.global.Maps;
import extension.util.translation.Google;
import extension.util.Log;
//GA-Exted

import static extension.core.Initialization.Follow_up_Initialization;
import static extension.data.json.Json.getData;
import static extension.net.HttpRequest.doGet;
import static extension.util.alone.BadWord.*;
import static extension.util.LocaleUtil.getinput;
import static extension.util.String_filteringUtil.*;
//Static

import com.alibaba.fastjson.JSONObject;
//Json

public class Event {

	public static void Main() {

		// 发送消息时
		Events.on(PlayerChatEvent.class, e -> {
			// 自动翻译
			Google googletranslation = new Google();
			if (!"/".equals(e.message.charAt(0))) {
			boolean valid = e.message.matches("\\w+");
			JSONObject date = getData("mods/GA/setting.json");
			// 检查是否启用翻译
			boolean translateo = (boolean) date.get("translateo");
				if (!valid && translateo) {
					final String translationa = googletranslation.translate(e.message,"en");
					Call.sendMessage("["+e.player.name+"]"+"[green] : [] "+translationa+"   -From Google Translator");
				}
			}

			// 去除语言检测

			if(Maps.getPlayer_power_Data(e.player.uuid)>0) {
				if(String.valueOf(e.message).equalsIgnoreCase("y") && !Vote.sted) {
					if (Vote.playerlist.contains(e.player.uuid)) {
						e.player.sendMessage("vote y");
					} else {
						Vote.playerlist.add(e.player.uuid);
						new Vote();	
					}
				}
			}
		});

		// 连接时
		Events.on(PlayerConnect.class, e -> {
			// 中英分检测  过滤屏蔽词
			Set<String> set = (Set<String>)BadWord(removeAll_EN(e.player.name));
			if (0 < set.size())
				Call.onKick(e.player.con, getinput("Sensitive.Thesaurus.join.kick",set.iterator().next()));
			Set<String> set1 = (Set<String>)BadWord(removeAll_CN(e.player.name));
			if (0 < set1.size())
				Call.onKick(e.player.con, getinput("Sensitive.Thesaurus.join.kick",set1.iterator().next()));
			
		});

		// 加入服务器时
		Events.on(PlayerJoin.class, e -> {
			while (result) {
				Call.onInfoMessage(e.player.con,"TEST");
			}
			
			// 设置队伍 登陆
			e.player.setTeam(Team.derelict);
			Call.onPlayerDeath(e.player);
			Maps.setPlayer_Data_Temp(e.player.uuid,"Playtime-start",String.valueOf(System.currentTimeMillis()));
		});

		// 退出时
		Events.on(PlayerLeave.class, e -> {
			Maps.removePlayer_Data_Temp(e.player.uuid,"Playtime-start");
		});

		// 游戏结束时
		Events.on(GameOverEvent.class, e -> {
			if (state.rules.pvp) {
				int index = 5;
				for (int a = 0; a < 5; a++) {
					if (state.teams.get(Team.all()[index]).cores.isEmpty()) {
						index--;
					}
				}
				if (index == 1) {
					for (int i = 0; i < playerGroup.size(); i++) {
						Player player = playerGroup.all().get(i);
						if (player.getTeam().name.equals(e.winner.name)) {
							//List<String> Pvpwincount = (List<String>)Maps.getPlayer_Data_SQL_Temp(player.uuid);
							System.out.println(player.uuid);
							if(Maps.getPlayer_power_Data(player.uuid)>0){
								//Maps.setPlayer_Data_SQL_Temp(player.uuid,Lists.updatePlayerData(Pvpwincount,SQL_type("Pvpwincount"),String.valueOf(Integer.parseInt(Pvpwincount.get(SQL_type("Pvpwincount")))+1)));
							}
						} else {
							//List<String> Pvpwincount = (List<String>)Maps.getPlayer_Data_SQL_Temp(player.uuid);
							System.out.println(player.uuid);
							if(Maps.getPlayer_power_Data(player.uuid)>0){
								//Maps.setPlayer_Data_SQL_Temp(player.uuid,Lists.updatePlayerData(Pvpwincount,SQL_type("Pvplosecount"),String.valueOf(Integer.parseInt(Pvpwincount.get(SQL_type("Pvplosecount")))+1)));
							}
						}
					}
				}
			}
		});

		//服务器加载完成时
		Events.on(ServerLoadEvent.class, e-> {
			netServer.admins.addChatFilter((player, message) -> {
				return replaceBadWord(message,2,"*");
			});

			netServer.assigner = ((player, players) -> {
				if (Vars.state.rules.pvp) {
					Teams.TeamData re = (Teams.TeamData)Vars.state.teams.getActive().min(data -> {
						int count = 0;
						for (final Player other : players)if (other.getTeam() == data.team && other != player)count++;
						if (!data.hasCore())count = 256;
						return (float)count;
					});
					return (null == re) ? null : re.team;
				}
				return Vars.state.rules.defaultTeam;
			});
			//linglan

			Follow_up_Initialization();
			//部分加载需要服务器加载完毕 例如maps
		});
	}

		//Call.onInfoToast(player.con,getinput("join.tourist",String.valueOf(TimeZone.getTimeZone((String)doGet("http://ip-api.com/line/"+Vars.netServer.admins.getInfo(player.uuid).lastIP+"?fields=timezone")).getRawOffset())),20f);

}