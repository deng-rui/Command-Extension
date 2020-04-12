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
import extension.data.global.Lists;
import extension.data.global.Maps;
import extension.util.translation.Bing;
import extension.util.translation.Google;
//GA-Exted

import static extension.core.ex.Extend.Authority_control;
import static extension.core.ex.Extend.loadmaps;
import static extension.core.ex.Extend.PlayerdatatoObject;

import static extension.data.db.Player.getSQLite;
import static extension.data.db.Player.isSQLite_User;
import static extension.data.db.Player.savePlayer;
import static extension.data.db.Player.InitializationPlayersSQLite;
import static extension.util.alone.Password.newPasswd;
import static extension.util.alone.Password.Passwdverify;
import static extension.util.DateUtil.getLocalTimeFromUTC;
import static extension.util.IsUtil.Blank;
import static extension.util.IsUtil.NotisNumeric;
import static extension.util.LocaleUtil.getinput;
import static extension.util.LocaleUtil.getinputt;
import static extension.util.LocaleUtil.Language_determination;
//Static

public class ClientCommandsx {

	public void register(CommandHandler handler) {
		//handler.removeCommand("help");
		handler.removeCommand("vote");
		handler.removeCommand("votekick");
/*
		handler.<Player>register("help", "[page]", "Displays this command list !", (args, player) -> {
			CommandHandler clientCommands = Vars.netServer.clientCommands;
			if(args.length > 0 && !Strings.canParseInt(args[0])){
				player.sendMessage("[scarlet]'page' must be a number.");
				return;
			}
			int commandsPerPage = 6;
			int page = args.length > 0 ? Strings.parseInt(args[0]) : 1;
			int pages = Mathf.ceil((float)clientCommands.getCommandList().size / commandsPerPage);
			page --;
			if(page >= pages || page < 0){
				player.sendMessage("[scarlet]'page' must be a number between[orange] 1[] and[orange] " + pages + "[scarlet].");
				return;
			}
			StringBuilder result = new StringBuilder();
			result.append(Strings.format("[orange]-- Commands Page[lightgray] {0}[gray]/[lightgray]{1}[orange] --\n\n", (page+1), pages));

			for(int i = commandsPerPage * page; i < Math.min(commandsPerPage * (page + 1), clientCommands.getCommandList().size); i++){
				Command command = clientCommands.getCommandList().get(i);
				result.append("[orange] /").append(command.text).append("[white] ").append(command.paramText).append("[lightgray] - ").append(command.description).append("\n");
			}
			player.sendMessage(result.toString());
		});
*/
		if (Config.Login) {
			handler.<Player>register("login", "<id> <password>", "4dV#-login", (args, player) -> {
				if (!Authority_control(player,"login"))
					player.sendMessage(getinput("authority.no"));
				else {
					PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
					if(playerdata.Login) {
						player.sendMessage(getinput("login.yes"));
						return;
					}
					if((boolean)isSQLite_User(args[0])) {
						player.sendMessage(getinput("login.usrno"));
						return;
					}
					PlayerData temp = new PlayerData("temp","temp",0);
					getSQLite(temp,args[0]);
					if(temp.Online) {
						player.sendMessage(getinput("login.in"));
						return;
					}
					try {
						if(!Passwdverify(args[1],temp.PasswordHash,temp.CSPRNG)) {
						player.sendMessage(getinput("login.pwno"));  
						return;
						}
					} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
						player.sendMessage(getinput("passwd.err"));
						return;
					}	
					getSQLite(playerdata,args[0]);
					playerdata.Joincount++;
					playerdata.Login=true;
					playerdata.LastLogin=getLocalTimeFromUTC(playerdata.GMT);
					if(!(playerdata.UUID).equals(player.uuid)) {
						playerdata.UUID = player.uuid;
						player.sendMessage(getinput("uuid.update"));
					}
					if (Vars.state.rules.pvp){
						player.setTeam(netServer.assignTeam(player, playerGroup.all()));
					} else {
						player.setTeam(Team.sharded);
					}
					if (Config.Login_Radical) {
						if (Vars.state.rules.pvp)
							player.setTeam(netServer.assignTeam(player, playerGroup.all()));
						else
							player.setTeam(Team.sharded);
						Call.onPlayerDeath(player);
					}
					Call.onInfoToast(player.con,getinput("join.start",getLocalTimeFromUTC(playerdata.GMT,playerdata.Time_format)),20f);
					Maps.setPlayer_Data(player.uuid,playerdata);
				}
			});

			handler.<Player>register("register", "<new_id> <new_password> <password_repeat> [your_mail]", "Login to account", (args, player) -> {
				if (!Authority_control(player,"register"))
					player.sendMessage(getinput("authority.no"));
				else {
					PlayerData playerdata = Maps.getPlayer_Data(player.uuid);
					if(playerdata.Login) {
						player.sendMessage(getinput("login.yes"));
						return;
					}
					if(!args[1].equals(args[2])) {
						player.sendMessage(getinput("register.pawno"));
						return;
					}
					if(!(boolean)isSQLite_User(args[0])) {
						player.sendMessage(getinput("register.usrerr"));
						return;
					}
					java.util.Map<String, Object> Passwd_date;
					try {
						Passwd_date = (java.util.Map<String, Object>)newPasswd(args[1]);
					} catch (Exception e) {
						player.sendMessage(getinput("passwd.err"));
						return;
					}
					if(!(boolean)Passwd_date.get("resualt")) {
						player.sendMessage(getinput("passwd.err"));
						return;
					}
					if (Config.Login_Radical) {
						if (Vars.state.rules.pvp)
							player.setTeam(netServer.assignTeam(player, playerGroup.all()));
						else
							player.setTeam(Team.sharded);
						Call.onPlayerDeath(player);
					}
					InitializationPlayersSQLite(args[0]);	
					playerdata.User=args[0];
					playerdata.Login=true;
					playerdata.Authority=1;
					playerdata.Mail=(args.length > 3) ? args[3]:"NULL";
					playerdata.PasswordHash=new String((String)Passwd_date.get("passwordHash"));
					playerdata.CSPRNG=new String((String)Passwd_date.get("salt"));
					playerdata.Joincount++;
					if(!Config.Login_IP) 
						PlayerData.playerip(Maps.getPlayer_Data(player.uuid),player,Vars.netServer.admins.getInfo(player.uuid).lastIP);
					playerdata.LastLogin=getLocalTimeFromUTC(playerdata.GMT);
					Call.onInfoToast(player.con,getinput("join.start",getLocalTimeFromUTC(playerdata.GMT,playerdata.Time_format)),20f);
				}
			});

			handler.<Player>register("ftpasswd", "<Email_at_registration> [Verification_Code]", "Forget password", (args, player) -> {
				if (!Authority_control(player,"ftpasswd"))
					player.sendMessage(getinput("authority.no"));
				else
				{}
					//ftpasswd(player,args[0],(args.length > 1) ? args[1] : null);
			});
		}
		//
		
		handler.<Player>register("info",getinput("info"), (args, player) -> {
			if (!Authority_control(player,"info")) {
				player.sendMessage(getinput("authority.no"));
			} else 
				Call.onInfoMessage(player.con,getinputt("info.info.1",PlayerdatatoObject(Maps.getPlayer_Data(player.uuid))));
		});

		handler.<Player>register("status",getinput("status"), (args, player) -> {
			if (!Authority_control(player,"status"))
				player.sendMessage(getinput("authority.no"));
			else
				player.sendMessage(getinput("status.info",playerGroup.size(),world.getMap().name(),Core.graphics.getFramesPerSecond(),Core.app.getJavaHeap()/1024/1024));
		});

		handler.<Player>register("tpp","<player> <player>",getinput("tpp"), (args, player) -> {
			if (!Authority_control(player,"tpp")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				try {
					int x = Integer.parseInt(args[0])*8;
					int y = Integer.parseInt(args[1])*8;
					player.setNet((float)x, (float)y);
					player.set((float)x, (float)y);
				} catch (Exception e){
					player.sendMessage(getinput("tpp.fail"));
				}
			}
		});

		handler.<Player>register("tp","<player...>",getinput("tp"), (args, player) -> {
			if (!Authority_control(player,"tp")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				Player other = playerGroup.find(p->p.name.equalsIgnoreCase(args[0]));
				if(null == other){
					player.sendMessage(getinput("tp.fail"));
					return;
				}
				player.setNet(other.x, other.y);
			}
		});

		handler.<Player>register("suicide",getinput("suicide"), (args, player) -> {
			if (!Authority_control(player,"suicide")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				player.onPlayerDeath(player);
				Call.sendMessage(getinput("suicide.tips",player.name));
			}
		});

		handler.<Player>register("team",getinput("team"), (args, player) ->{
			if (!Authority_control(player,"team")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				if (!state.rules.pvp){
					player.sendMessage(getinput("team.fail"));
					return;
				}
				int index = player.getTeam().id+1;
				player.sendMessage(String.valueOf(index));
				while (index != player.getTeam().id){
					if (index >= Team.all().length){
						index = 0;
					}
					if (!state.teams.get(Team.all()[index]).cores.isEmpty()){
						player.setTeam(Team.all()[index]);
						break;
					}
					index++;
				}
				Call.onPlayerDeath(player);
			}
		});

		handler.<Player>register("difficulty", "<difficulty>", getinput("difficulty"), (args, player) -> {
			if (!Authority_control(player,"difficulty")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				try {
					Difficulty.valueOf(args[0]);
					player.sendMessage(getinput("difficulty.success",args[0]));
				}catch(IllegalArgumentException e) {
					player.sendMessage(getinput("difficulty.fail",args[0]));
				}
			}
		});

		handler.<Player>register("gameover",getinput("gameover"), (args, player) -> {
			if (!Authority_control(player,"gameover")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				Events.fire(new GameOverEvent(Team.crux));
			}
		});


		handler.<Player>register("host","<map_number>",getinput("host"), (args, player) -> {
			if (Authority_control(player,"host")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				host(player,args[0]);
			}
		});

		handler.<Player>register("runwave",getinput("runwave"), (args, player) -> {
			if (!Authority_control(player,"runwave")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				logic.runWave();
			}
		});

		handler.<Player>register("time",getinput("time"), (args, player) -> {
			player.sendMessage(getinput("time.info",getLocalTimeFromUTC(0,1)));
		});

		handler.<Player>register("tr","<text> [Output-language]",getinput("tr"), (args, player) -> {
			if (!Authority_control(player,"tr")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				final Google googletranslation = new Google();
				//No spaces are allowed in the input language??
				player.sendMessage(getinput("tr.tips"));
				player.sendMessage(getinput("tr.tips1"));
				try {
					Thread.currentThread().sleep(2500);
				}catch(InterruptedException ie){
					ie.printStackTrace();
				}
				// 默认EN
				Call.sendMessage("["+player.name+"]"+"[green] : [] "+googletranslation.translate(args[0].replace('_',' '),(Blank(args[1])) ? "en" : args[1])+"   -From Google Translator");
			}	
		});

		handler.<Player>register("maps", "[page] [mode]", getinput("maps"), (args, player) -> {
			if (!Authority_control(player,"maps")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				if(NotisNumeric(args.length > 0?args[0]:"1")) {
					player.sendMessage(getinput("nber.err"));
					return;
				}
				List<String> MapsList = (List<String>)Lists.getMaps_List();
				int Maximum = 6;
				//6为list最大承载 可自行改
				int page = args.length > 0 ? Integer.parseInt(args[0]):1;
				int pages = Mathf.ceil((float)MapsList.size() / Maximum);
				page --;
				if(page >= pages || page < 0){
					player.sendMessage(getinput("maps.page.err",pages));
					return;
				}
				if(args.length == 2) {
					player.sendMessage(getinput("maps.page",(page+1),pages));
					for(int i = Maximum * page; i < Math.min(Maximum * (page + 1), MapsList.size()); i++){
						String [] data = MapsList.get(i).split("\\s+");
						if(data[3].equalsIgnoreCase(args[1]))player.sendMessage(getinput("maps.mode.info",String.valueOf(i),data[0],data[1]));
					}
					return;
				}
				player.sendMessage(getinput("maps.page",(page+1),pages));
				for(int i = Maximum * page; i < Math.min(Maximum * (page + 1), MapsList.size()); i++){
					String [] data = MapsList.get(i).split("\\s+");
					player.sendMessage(getinput("maps.info",String.valueOf(i),data[0],data[1],data[2]));
				}
			}
		});

		handler.<Player>register("vote", "<help> [parameter]", getinput("vote"), (args, player) -> {
			if (!Authority_control(player,"vote")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				if(!Vote.sted) {
					player.sendMessage(getinput("vote.already_begun"));
					return;
				}
				switch(args[0]) {
					case "help":
						Call.onInfoToast(player.con,getinput("vote.help"),40f);
						break;
					case "gameover":
					case "skipwave":
						new Vote(player,args[0]);
						break;
					case "kick":
						if (args.length > 1)
							new Vote(player,args[0],args[1]);
						else
							player.sendMessage(getinput("args.err"));
						break;
					case "ff":
						new Vote(player,args[0],player.getTeam());
						break;
					case "host":
						if (args.length > 1)
							if(NotisNumeric(args[1])) 
								player.sendMessage(getinput("nber.err"));
							else
								new Vote(player,args[0],args[1]);
						else
							player.sendMessage(getinput("args.err"));
						break;
					default:
						player.sendMessage(getinput("vote.err",args[0]));
						break;
				}
			}
		});

		handler.<Player>register("votekick", "<player>", getinput("maps"), (args, player) -> {
			if (!Authority_control(player,"votekick")) {
				player.sendMessage(getinput("authority.no"));
			} else {
				new Vote(player,"kick",args[0]);
			}
		});
	}

	private static void host(Player player, String mapss) {
		if (!(Lists.getMaps_List().size() >= Integer.parseInt(mapss))) {
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

}