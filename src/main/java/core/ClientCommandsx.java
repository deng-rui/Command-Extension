package extension.core;

import java.lang.Math;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
//Java

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.util.CommandHandler;
import arc.util.CommandHandler.Command;
//Arc

import mindustry.entities.type.Player;
import mindustry.gen.Call;
import mindustry.game.EventType.GameOverEvent;
import mindustry.game.Difficulty;
import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.maps.Map;
import mindustry.Vars;
//Mindustry

import static mindustry.Vars.logic;
import static mindustry.Vars.maps;
import static mindustry.Vars.netServer;
import static mindustry.Vars.playerGroup;
import static mindustry.Vars.state;
import static mindustry.Vars.world;
//Mindustry-Static

import extension.core.ex.Vote;
import extension.data.db.PlayerData;
import extension.data.global.Config;
import extension.data.global.Data;
import extension.data.global.Lists;
import extension.data.global.Maps;
import extension.util.translation.Bing;
import extension.util.translation.Google;
import extension.util.LocaleUtil;
import extension.util.log.Log;
//GA-Exted

import static extension.core.ex.Extend.Authority_control;
import static extension.core.ex.Extend.loadmaps;
import static extension.core.ex.Extend.PlayerdatatoObject;
import static extension.core.ex.Threads.NewThred_DB;

import static extension.data.db.Player.getSQLite;
import static extension.data.db.Player.isSQLite_User;
import static extension.data.db.Player.savePlayer;
import static extension.data.db.Player.InitializationPlayersSQLite;
import static extension.util.alone.Password.newPasswd;
import static extension.util.alone.Password.Passwdverify;
import static extension.util.DateUtil.getLocalTimeFromUTC;
import static extension.util.ExtractUtil.Language_determination;
import static extension.util.IsUtil.Blank;
import static extension.util.IsUtil.NotisNumeric;
//Static

public class ClientCommandsx {

	public void register(CommandHandler handler) {
		handler.removeCommand("help");
		handler.removeCommand("vote");
		handler.removeCommand("votekick");

		handler.<Player>register("help", "[page]", "Displays this command list !", (args, player) -> {
			CommandHandler clientCommands = Vars.netServer.clientCommands;
			LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
			if(NotisNumeric(args.length > 0?args[0]:"1")) {
				player.sendMessage(localeUtil.getinput("nber.err"));
				return;
			}
			int page = args.length > 0 ? Integer.parseInt(args[0]) : 1;
			int pages = Mathf.ceil((float)clientCommands.getCommandList().size / Config.Maximum_Screen_Display);
			page --;
			if(page >= pages || page < 0){
				player.sendMessage("[scarlet]'page' must be a number between[orange] 1[] and[orange] " + pages + "[scarlet].");
				return;
			}
			StringBuilder result = new StringBuilder();
			result.append("[orange]-- Commands Page[lightgray] "+(page+1)+" [gray]/[lightgray] "+pages+" [orange] --\n\n");
			Command command;
			if(Config.Help_Show_unauthorize_content) {
				for(int i = Config.Maximum_Screen_Display * page; i < Math.min(Config.Maximum_Screen_Display * (page + 1), clientCommands.getCommandList().size); i++){
					command = clientCommands.getCommandList().get(i);
					if("4dV#-".equals(command.description.substring(0,5))) 
						result.append("[orange] /").append(command.text).append("[white] ").append(command.paramText).append("[lightgray] - ").append(localeUtil.getinput(command.description.substring(5,command.description.length()))).append("\n");
					else
						result.append("[orange] /").append(command.text).append("[white] ").append(command.paramText).append("[lightgray] - ").append(command.description).append("\n");
				}
			} else {
				for(int i = Config.Maximum_Screen_Display * page; i < Math.min(Config.Maximum_Screen_Display * (page + 1), clientCommands.getCommandList().size); i++){
					command = clientCommands.getCommandList().get(i);
					if("4dV#-".equals(command.description.substring(0,5))) {
						if(Authority_control(player,command.text)) 
							result.append("[orange] /").append(command.text).append("[white] ").append(command.paramText).append("[lightgray] - ").append(localeUtil.getinput(command.description.substring(5,command.description.length()))).append("\n");
					} else
						result.append("[orange] /").append(command.text).append("[white] ").append(command.paramText).append("[lightgray] - ").append(command.description).append("\n");
				}
			}
			player.sendMessage(result.toString());
		});

		if(Config.Login) {
			handler.<Player>register("login", "<id> <password>", "4dV#-login", (args, player) -> {
				LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
				if(!Authority_control(player,"login"))
					player.sendMessage(localeUtil.getinput("authority.no"));
				else {
					final String id = args[0];
					final String pw = args[1];
					Data.Thred_service.execute(new Runnable() {
						@Override
						public void run() {
							PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
							if(playerdata.Login) {
								player.sendMessage(localeUtil.getinput("login.yes"));
								return;
							}
							if((boolean)isSQLite_User(id)) {
								player.sendMessage(localeUtil.getinput("login.usrno"));
								return;
							}
							PlayerData temp = new PlayerData("temp","temp",0);
							getSQLite(temp,id);
							if(temp.Online) {
								player.sendMessage(localeUtil.getinput("login.in"));
								return;
							}
							try {
								if(!Passwdverify(pw,temp.PasswordHash,temp.CSPRNG)) {
								player.sendMessage(localeUtil.getinput("login.pwno"));  
								return;
								}
							} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
								player.sendMessage(localeUtil.getinput("passwd.err"));
								return;
							}	
							getSQLite(playerdata,id);
							playerdata.Login=true;
							playerdata.Online=true;							
							playerdata.LastLogin=getLocalTimeFromUTC(playerdata.GMT);
							if(!(playerdata.UUID).equals(player.uuid)) {
								playerdata.UUID = player.uuid;
								player.sendMessage(localeUtil.getinput("uuid.update"));
							}
							if(Config.Login_Radical) {
								if(Vars.state.rules.pvp)
									player.setTeam(netServer.assignTeam(player, playerGroup.all()));
								else
									player.setTeam(Team.sharded);
								//Call.onPlayerDeath(player);
								player.kill();
							}
							player.sendMessage(localeUtil.getinput("login.to"));
							//Call.onInfoToast(player.con,getinput("join.start",getLocalTimeFromUTC(playerdata.GMT,playerdata.Time_format)),20f);
							Maps.setPlayer_Data(player.uuid,playerdata);
							NewThred_DB(() -> savePlayer(playerdata,playerdata.User));
						}
					});
				}
			});

			handler.<Player>register("register", "<new_id> <new_password> <password_repeat> [your_mail]", "4dV#-register", (args, player) -> {
				LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
				if(!Authority_control(player,"register"))
					player.sendMessage(localeUtil.getinput("authority.no"));
				else {
					final String newid = args[0];
					final String newpw = args[1];
					final String renewpw = args[2];
					final String mail = (args.length > 3) ? args[3]:"NULL";
					Data.Thred_service.execute(new Runnable() {
						@Override
						public void run() {
							PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
							if(playerdata.Login) {
								player.sendMessage(localeUtil.getinput("login.yes"));
								return;
							}
							if(!newpw.equals(renewpw)) {
								player.sendMessage(localeUtil.getinput("register.pawno"));
								return;
							}
							if(!(boolean)isSQLite_User(newid)) {
								player.sendMessage(localeUtil.getinput("register.usrerr"));
								return;
							}
							java.util.Map<String, Object> Passwd_date;
							try {
								Passwd_date = (java.util.Map<String, Object>)newPasswd(newpw);
							} catch (Exception e) {
								player.sendMessage(localeUtil.getinput("passwd.err"));
								return;
							}
							if(!(boolean)Passwd_date.get("resualt")) {
								player.sendMessage(localeUtil.getinput("passwd.err"));
								return;
							}
							if(Config.Login_Radical) {
								if(Vars.state.rules.pvp)
									player.setTeam(netServer.assignTeam(player, playerGroup.all()));
								else
									player.setTeam(Team.sharded);
								//Call.onPlayerDeath(player);
								player.kill();
							}
							InitializationPlayersSQLite(newid);	
							playerdata.User=newid;
							playerdata.Login=true;
							playerdata.Authority=1;
							playerdata.Mail=mail;
							playerdata.PasswordHash=(String)Passwd_date.get("passwordHash");
							playerdata.CSPRNG=(String)Passwd_date.get("salt");
							playerdata.LastLogin=getLocalTimeFromUTC(playerdata.GMT);
							player.sendMessage(localeUtil.getinput("register.to"));
							//Call.onInfoToast(player.con,getinput("join.start",getLocalTimeFromUTC(playerdata.GMT,playerdata.Time_format)),20f);
							NewThred_DB(() -> savePlayer(playerdata,playerdata.User));
						}
					});
				}
			});

			handler.<Player>register("ftpasswd", "<Email_at_registration> [Verification_Code]", "4dV#-ftpasswd", (args, player) -> {
				LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
				if(!Authority_control(player,"ftpasswd"))
					player.sendMessage(localeUtil.getinput("authority.no"));
				else
				{}
					//ftpasswd(player,args[0],(args.length > 1) ? args[1] : null);
			});
		}
		//
		
		handler.<Player>register("info","4dV#-info", (args, player) -> {
			LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
			if(!Authority_control(player,"info")) {
				player.sendMessage(localeUtil.getinput("authority.no"));
			} else 
				Call.onInfoMessage(player.con,localeUtil.getinputt("info.info.1",PlayerdatatoObject(Maps.getPlayer_Data(player.uuid))));
		});

		handler.<Player>register("status","4dV#-status", (args, player) -> {
			LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
			if(!Authority_control(player,"status"))
				player.sendMessage(localeUtil.getinput("authority.no"));
			else
				player.sendMessage(localeUtil.getinput("status.info",playerGroup.size(),world.getMap().name(),Core.graphics.getFramesPerSecond(),Core.app.getJavaHeap()/1024/1024));
		});

		handler.<Player>register("tpp","<player> <player>","4dV#-tpp", (args, player) -> {
			LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
			if(!Authority_control(player,"tpp")) {
				player.sendMessage(localeUtil.getinput("authority.no"));
			} else {
				if(player.isMobile) {
					player.sendMessage(localeUtil.getinput("mob.no"));
					return;
				}
				try {
					int x = Integer.parseInt(args[0])*8;
					int y = Integer.parseInt(args[1])*8;
					player.setNet((float)x, (float)y);
					player.set((float)x, (float)y);
				} catch (Exception e){
					player.sendMessage(localeUtil.getinput("tpp.fail"));
				}
			}
		});

		handler.<Player>register("tp","<player...>","4dV#-tp", (args, player) -> {
			LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
			if(!Authority_control(player,"tp")) {
				player.sendMessage(localeUtil.getinput("authority.no"));
			} else {
				if(player.isMobile) {
					player.sendMessage(localeUtil.getinput("mob.no"));
					return;
				}
				Player other = playerGroup.find(p->p.name.equalsIgnoreCase(args[0]));
				if(null == other)
					player.sendMessage(localeUtil.getinput("tp.fail"));
				else
					player.setNet(other.x, other.y);
			}
		});

		handler.<Player>register("suicide","4dV#-suicide", (args, player) -> {
			LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
			if(!Authority_control(player,"suicide")) {
				player.sendMessage(localeUtil.getinput("authority.no"));
			} else {
				player.onPlayerDeath(player);
				Call.sendMessage(localeUtil.getinput("suicide.tips",player.name));
			}
		});

		handler.<Player>register("team","4dV#-team", (args, player) ->{
			LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
			if(!Authority_control(player,"team")) {
				player.sendMessage(localeUtil.getinput("authority.no"));
			} else {
				if(!state.rules.pvp){
					player.sendMessage(localeUtil.getinput("team.fail"));
					return;
				}
				int index = player.getTeam().id+1;
				player.sendMessage(String.valueOf(index));
				while (index != player.getTeam().id){
					if(index >= Team.all().length){
						index = 0;
					}
					if(!state.teams.get(Team.all()[index]).cores.isEmpty()){
						player.setTeam(Team.all()[index]);
						break;
					}
					index++;
				}
				player.kill();
			}
		});

		handler.<Player>register("difficulty", "<difficulty>","4dV#-difficulty", (args, player) -> {
			LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
			if(!Authority_control(player,"difficulty")) {
				player.sendMessage(localeUtil.getinput("authority.no"));
			} else {
				try {
					Difficulty.valueOf(args[0]);
					player.sendMessage(localeUtil.getinput("difficulty.success",args[0]));
				}catch(IllegalArgumentException e) {
					player.sendMessage(localeUtil.getinput("difficulty.fail",args[0]));
				}
			}
		});

		handler.<Player>register("gameover","4dV#-gameover", (args, player) -> {
			if(!Authority_control(player,"gameover")) {
				player.sendMessage(Maps.getPlayer_Data(player.uuid).Info.getinput("authority.no"));
			} else {
				Events.fire(new GameOverEvent(Team.crux));
			}
		});

/*
		handler.<Player>register("host","<map_number>",getinput("host"), (args, player) -> {
			if(Authority_control(player,"host")) {
				player.sendMessage(localeUtil.getinput("authority.no"));
			} else {
				host(player,args[0]);
			}
		});
*/
		handler.<Player>register("runwave","4dV#-runwave", (args, player) -> {
			if(!Authority_control(player,"runwave")) {
				player.sendMessage(Maps.getPlayer_Data(player.uuid).Info.getinput("authority.no"));
			} else {
				logic.runWave();
			}
		});

		handler.<Player>register("time","4dV#-time", (args, player) -> {
			player.sendMessage(Maps.getPlayer_Data(player.uuid).Info.getinput("time.info",getLocalTimeFromUTC(0,1)));
		});

		handler.<Player>register("tr","<Output-language> <text...>","4dV#-tr", (args, player) -> {
			LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
			if(!Authority_control(player,"tr")) {
				player.sendMessage(localeUtil.getinput("authority.no"));
			} else {
				player.sendMessage(localeUtil.getinput("tr.tips"));
				// 默认EN
				Call.sendMessage("["+player.name+"]"+"[green] : [] [white]"+new Google().translate(args[1],(Blank(args[0])) ? "en" : args[1] )+"   -From Google Translator");
			}	
		});

		handler.<Player>register("maps", "[page] [mode]","4dV#-maps", (args, player) -> {
			LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
			if(!Authority_control(player,"maps")) {
				player.sendMessage(localeUtil.getinput("authority.no"));
			} else {
				if(NotisNumeric(args.length > 0?args[0]:"1")) {
					player.sendMessage(localeUtil.getinput("nber.err"));
					return;
				}
				List<String> MapsList = (List<String>)Lists.getMaps_List();
				int page = args.length > 0 ? Integer.parseInt(args[0]):1;
				int pages = Mathf.ceil((float)MapsList.size() / Config.Maximum_Screen_Display);
				page --;
				if(page >= pages || page < 0){
					player.sendMessage(localeUtil.getinput("maps.page.err",pages));
					return;
				}
				if(args.length == 2) {
					player.sendMessage(localeUtil.getinput("maps.page",(page+1),pages));
					for(int i = Config.Maximum_Screen_Display * page; i < Math.min(Config.Maximum_Screen_Display * (page + 1), MapsList.size()); i++){
						String [] data = MapsList.get(i).split("\\s+");
						if(data[3].equalsIgnoreCase(args[1]))player.sendMessage(localeUtil.getinput("maps.mode.info",String.valueOf(i),data[0],data[1]));
					}
					return;
				}
				player.sendMessage(localeUtil.getinput("maps.page",(page+1),pages));
				for(int i = Config.Maximum_Screen_Display * page; i < Math.min(Config.Maximum_Screen_Display * (page + 1), MapsList.size()); i++){
					String [] data = MapsList.get(i).split("\\s+");
					player.sendMessage(localeUtil.getinput("maps.info",String.valueOf(i),data[0],data[1],data[2]));
				}
			}
		});

		handler.<Player>register("vote", "<help> [parameter]","4dV#-vote", (args, player) -> {
			LocaleUtil localeUtil = Maps.getPlayer_Data(player.uuid).Info;
			if(!Authority_control(player,"vote")) {
				player.sendMessage(localeUtil.getinput("authority.no"));
			} else {
				if(!Vote.sted) {
					player.sendMessage(localeUtil.getinput("vote.already_begun"));
					return;
				}
				switch(args[0].toLowerCase()) {
					case "help":
						Call.onInfoToast(player.con,localeUtil.getinput("vote.help"),40f);
						break;
					case "gameover":
					case "skipwave":
						Data.vote = new Vote(player,args[0]);
						break;
					case "kick":
						if(args.length > 1)
							Data.vote = new Vote(player,args[0],args[1]);
						else
							player.sendMessage(localeUtil.getinput("args.err"));
						break;
					case "ff":
						Data.vote = new Vote(player,args[0],player.getTeam());
						break;
					case "host":
						if(args.length > 1)
							if(NotisNumeric(args[1])) {
								player.sendMessage(localeUtil.getinput("nber.err"));
							} else
								if(!(Lists.getMaps_List().size() >= Integer.parseInt(args[1]))) {
									player.sendMessage(localeUtil.getinput("vote.host.maps.err",args[1]));
								} else
									Data.vote = new Vote(player,args[0],args[1]);
						else
							player.sendMessage(localeUtil.getinput("args.err"));
						break;
					default:
						player.sendMessage(localeUtil.getinput("vote.err",args[0]));
						break;
				}
			}
		});

		handler.<Player>register("votekick", "<player>","4dV#-maps", (args, player) -> {
			if(!Authority_control(player,"votekick")) {
				player.sendMessage(Maps.getPlayer_Data(player.uuid).Info.getinput("authority.no"));
			} else {
				new Vote(player,"kick",args[0]);
			}
		});
	}
/*
	private static void host(Player player, String mapss) {
		if(!(Lists.getMaps_List().size() >= Integer.parseInt(mapss))) {
			player.sendMessage(getinput("vote.host.maps.err",mapss));
			return;
		}
		List<String> MapsList = (List<String>)Lists.getMaps_List();
		String [] data = MapsList.get(Integer.parseInt(mapss)).split("\\s+");
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
*/
}