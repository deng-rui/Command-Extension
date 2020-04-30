package extension.core;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.util.CommandHandler;
import arc.util.CommandHandler.Command;
import extension.core.ex.Vote;
import extension.data.db.PlayerData;
import extension.data.global.Config;
import extension.data.global.Data;
import extension.data.global.Lists;
import extension.data.global.Maps;
import extension.util.LocaleUtil;
import extension.util.translation.Google;
import mindustry.Vars;
import mindustry.entities.type.Player;
import mindustry.game.Difficulty;
import mindustry.game.EventType.GameOverEvent;
import mindustry.game.Team;
import mindustry.gen.Call;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static extension.core.ex.Extend.authorityControl;
import static extension.core.ex.Extend.playerdatatoObject;
import static extension.core.ex.Threads.newThredDb;
import static extension.data.db.Player.*;
import static extension.util.DateUtil.getLocalTimeFromU;
import static extension.util.DateUtil.longtoTime;
import static extension.util.IsUtil.isBlank;
import static extension.util.IsUtil.notisNumeric;
import static extension.util.alone.Password.isPasswdVerify;
import static extension.util.alone.Password.newPasswd;
import static mindustry.Vars.*;

/**
 * @author Dr
 * @Date ?
 */
public class ClientCommandsx {


    public void register(CommandHandler handler) {
        handler.removeCommand("help");
        handler.removeCommand("vote");
        handler.removeCommand("votekick");

        handler.<Player>register("help", "[page]", "Displays this command list !", (args, player) -> {
            CommandHandler clientCommands = Vars.netServer.clientCommands;
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String def = "1";
            if (notisNumeric(args.length > 0 ? args[0] : def)) {
                player.sendMessage(localeUtil.getinput("nber.err"));
                return;
            }
            int page = args.length > 0 ? Integer.parseInt(args[0]) : 1;
            int pages = Mathf.ceil((float) clientCommands.getCommandList().size / Config.MAXIMUM_SCREEN_DISPLAY);
            page--;
            if (page >= pages || page < 0) {
                player.sendMessage("[scarlet]'page' must be a number between[orange] 1[] and[orange] " + pages + "[scarlet].");
                return;
            }
            StringBuilder result = new StringBuilder();
            result.append("[orange]-- Commands Page[lightgray] " + (page + 1) + " [gray]/[lightgray] " + pages + " [orange] --\n\n");
            Command command;
            if (Config.HELP_SHOW_UNAUTHORIZE_CONTENT) {
                for (int i = Config.MAXIMUM_SCREEN_DISPLAY * page; i < Math.min(Config.MAXIMUM_SCREEN_DISPLAY * (page + 1), clientCommands.getCommandList().size); i++) {
                    command = clientCommands.getCommandList().get(i);
                    if ("4dV#-".equals(command.description.substring(0, 5))) {
                        result.append("[orange] /").append(command.text).append("[white] ").append(command.paramText).append("[lightgray] - ").append(localeUtil.getinput(command.description.substring(5, command.description.length()))).append("\n");
                    } else {
                        result.append("[orange] /").append(command.text).append("[white] ").append(command.paramText).append("[lightgray] - ").append(command.description).append("\n");
                    }
                }
            } else {
                int count = 0;
                for (int i = Config.MAXIMUM_SCREEN_DISPLAY * page; i < clientCommands.getCommandList().size; i++) {
                    command = clientCommands.getCommandList().get(i);
                    if (count == 6) {
                        break;
                    }
                    count++;
                    if ("4dV#-".equals(command.description.substring(0, 5))) {
                        if (authorityControl(player, command.text)) {
                            result.append("[orange] /").append(command.text).append("[white] ").append(command.paramText).append("[lightgray] - ").append(localeUtil.getinput(command.description.substring(5, command.description.length()))).append("\n");
                        } else {
                            count--;
                        }
                    } else {
                        result.append("[orange] /").append(command.text).append("[white] ").append(command.paramText).append("[lightgray] - ").append(command.description).append("\n");
                    }
                }
            }
            player.sendMessage(result.toString());
        });

        handler.<Player>register("login", "<id> <password>", "4dV#-login", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "login";
            if (authorityControl(player, commid)) {
                final String id = args[0];
                final String pw = args[1];
                Data.THRED_SERVICE.execute(new Runnable() {
                    @Override
                    public void run() {
                        PlayerData playerdata = Maps.getPlayerData(player.uuid);
                        if (playerdata.login) {
                            player.sendMessage(localeUtil.getinput("login.yes"));
                            return;
                        }
                        if ((boolean) isSqliteUser(id)) {
                            player.sendMessage(localeUtil.getinput("login.usrno"));
                            return;
                        }
                        PlayerData temp = new PlayerData("temp", "temp", 0);
                        getSqlite(temp, id);
                        if (temp.online) {
                            player.sendMessage(localeUtil.getinput("login.in"));
                            return;
                        }
                        try {
                            if (!isPasswdVerify(pw, temp.passwordHash, temp.csprng)) {
                                player.sendMessage(localeUtil.getinput("login.pwno"));
                                return;
                            }
                        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                            player.sendMessage(localeUtil.getinput("passwd.err"));
                            return;
                        }
                        getSqlite(playerdata, id);
                        playerdata.login = true;
                        playerdata.online = true;
                        playerdata.lastLogin = getLocalTimeFromU(playerdata.gmt);
                        if (!(playerdata.uuid).equals(player.uuid)) {
                            playerdata.uuid = player.uuid;
                            player.sendMessage(localeUtil.getinput("uuid.update"));
                        }
                        if (Config.LOGIN_RADICAL) {
                            if (Vars.state.rules.pvp) {
                                player.setTeam(netServer.assignTeam(player, playerGroup.all()));
                            } else {
                                player.setTeam(Team.sharded);
                            }
                            player.kill();
                        }
                        player.sendMessage(localeUtil.getinput("login.to"));
                        Maps.setPlayerData(player.uuid, playerdata);
                        newThredDb(() -> {
                            PlayerData.playerip(playerdata, player, Vars.netServer.admins.getInfo(player.uuid).lastIP);
                            savePlayer(playerdata, playerdata.user);
                        });
                    }
                });
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
        });

        handler.<Player>register("register", "<new_id> <new_password> <password_repeat> [your_mail]", "4dV#-register", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "register";
            if (authorityControl(player, commid)) {
                final String newid = args[0];
                final String newpw = args[1];
                final String renewpw = args[2];
                final String mail = (args.length > 3) ? args[3] : "NULL";
                Data.THRED_SERVICE.execute(new Runnable() {
                    @Override
                    public void run() {
                        PlayerData playerdata = Maps.getPlayerData(player.uuid);
                        if (playerdata.login) {
                            player.sendMessage(localeUtil.getinput("login.yes"));
                            return;
                        }
                        if (!newpw.equals(renewpw)) {
                            player.sendMessage(localeUtil.getinput("register.pawno"));
                            return;
                        }
                        if (!(boolean) isSqliteUser(newid)) {
                            player.sendMessage(localeUtil.getinput("register.usrerr"));
                            return;
                        }
                        java.util.Map<String, Object> passwdDate;
                        try {
                            passwdDate = (java.util.Map<String, Object>) newPasswd(newpw);
                        } catch (Exception e) {
                            player.sendMessage(localeUtil.getinput("passwd.err"));
                            return;
                        }
                        boolean result = (boolean) passwdDate.get("resualt");
                        if (result) {
                            if (Config.LOGIN_RADICAL) {
                                if (Vars.state.rules.pvp) {
                                    player.setTeam(netServer.assignTeam(player, playerGroup.all()));
                                } else {
                                    player.setTeam(Team.sharded);
                                }
                                player.kill();
                            }
                            initPlayersSqlite(newid);
                            playerdata.user = newid;
                            playerdata.login = true;
                            playerdata.authority = 1;
                            playerdata.mail = mail;
                            playerdata.passwordHash = (String) passwdDate.get("passwordHash");
                            playerdata.csprng = (String) passwdDate.get("salt");
                            playerdata.lastLogin = getLocalTimeFromU(playerdata.gmt);
                            player.sendMessage(localeUtil.getinput("register.to"));
                            newThredDb(() -> {
                                PlayerData.playerip(playerdata, player, Vars.netServer.admins.getInfo(player.uuid).lastIP);
                                savePlayer(playerdata, playerdata.user);
                            });
                        } else {
                            player.sendMessage(localeUtil.getinput("passwd.err"));
                            return;
                        }
                    }
                });
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
        });

        handler.<Player>register("ftpasswd", "<Email_at_registration> [Verification_Code]", "4dV#-ftpasswd", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "ftpasswd";
            if (authorityControl(player, commid)) {
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
            //ftpasswd(player,args[0],(args.length > 1) ? args[1] : null);
        });
        //

        handler.<Player>register("info", "4dV#-info", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "info";
            if (authorityControl(player, commid)) {
                Call.onInfoMessage(player.con, localeUtil.getinputt("info.info.1", playerdatatoObject(Maps.getPlayerData(player.uuid))));
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
        });

        handler.<Player>register("status", "4dV#-status", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "status";
            if (authorityControl(player, commid)) {
                player.sendMessage(localeUtil.getinput("status.info", playerGroup.size(), world.getMap().name(), Core.graphics.getFramesPerSecond(), Core.app.getJavaHeap() / 1024 / 1024));
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
        });

        handler.<Player>register("tpp", "<player> <player>", "4dV#-tpp", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "tpp";
            if (authorityControl(player, commid)) {
                if (player.isMobile) {
                    player.sendMessage(localeUtil.getinput("mob.no"));
                    return;
                }
                try {
                    int x = Integer.parseInt(args[0]) << 3;
                    int y = Integer.parseInt(args[1]) << 3;
                    player.setNet((float) x, (float) y);
                    player.set((float) x, (float) y);
                } catch (Exception e) {
                    player.sendMessage(localeUtil.getinput("tpp.fail"));
                }
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
        });

        handler.<Player>register("tp", "<player...>", "4dV#-tp", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "tp";
            if (authorityControl(player, commid)) {
                if (player.isMobile) {
                    player.sendMessage(localeUtil.getinput("mob.no"));
                    return;
                }
                Player other = playerGroup.find(p -> p.name.equalsIgnoreCase(args[0]));
                if (null == other) {
                    player.sendMessage(localeUtil.getinput("tp.fail"));
                } else {
                    player.setNet(other.x, other.y);
                }
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
        });

        handler.<Player>register("suicide", "4dV#-suicide", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "suicide";
            if (authorityControl(player, commid)) {
                Player.onPlayerDeath(player);
                Call.sendMessage(localeUtil.getinput("suicide.tips", player.name));
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
        });

        handler.<Player>register("team", "4dV#-team", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "team";
            if (authorityControl(player, commid)) {
                if (!state.rules.pvp) {
                    player.sendMessage(localeUtil.getinput("team.fail"));
                    return;
                }
                int index = player.getTeam().id + 1;
                player.sendMessage(String.valueOf(index));
                while (index != player.getTeam().id) {
                    if (index >= Team.all().length) {
                        index = 0;
                    }
                    if (!state.teams.get(Team.all()[index]).cores.isEmpty()) {
                        player.setTeam(Team.all()[index]);
                        break;
                    }
                    index++;
                }
                player.kill();
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
        });

        handler.<Player>register("difficulty", "<difficulty>", "4dV#-difficulty", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "difficulty";
            if (authorityControl(player, commid)) {
                try {
                    Difficulty.valueOf(args[0]);
                    player.sendMessage(localeUtil.getinput("difficulty.success", args[0]));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(localeUtil.getinput("difficulty.fail", args[0]));
                }
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
        });

        handler.<Player>register("gameover", "4dV#-gameover", (args, player) -> {
            final String commid = "gameover";
            if (authorityControl(player, commid)) {
                Events.fire(new GameOverEvent(Team.crux));
            } else {
                player.sendMessage(Maps.getPlayerData(player.uuid).info.getinput("authority.no"));
            }
        });

/*
		handler.<Player>register("host","<map_number>",getinput("host"), (args, player) -> {
			if(authorityControl(player,"host")) {
				player.sendMessage(localeUtil.getinput("authority.no"));
			} else {
				host(player,args[0]);
			}
		});
*/
        handler.<Player>register("runwave", "4dV#-runwave", (args, player) -> {
            final String commid = "runwave";
            if (authorityControl(player, commid)) {
                logic.runWave();
            } else {
                player.sendMessage(Maps.getPlayerData(player.uuid).info.getinput("authority.no"));
            }
        });

        handler.<Player>register("time", "4dV#-time", (args, player) -> {
            player.sendMessage(Maps.getPlayerData(player.uuid).info.getinput("time.info", getLocalTimeFromU(0, 1)));
        });

        handler.<Player>register("tr", "<Output-language> <text...>", "4dV#-tr", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "tr";
            if (authorityControl(player, commid)) {
                player.sendMessage(localeUtil.getinput("tr.tips"));
                // 默认EN
                Call.sendMessage("[" + player.name + "]" + "[green] : [] [white]" + new Google().translate(args[1], (isBlank(args[0])) ? "en" : args[1]) + "   -From Google Translator");
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
        });

        handler.<Player>register("maps", "[page] [mode]", "4dV#-maps", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "maps";
            if (authorityControl(player, commid)) {
                final String def = "1";
                if (notisNumeric((0 < args.length) ? args[0] : def)) {
                    player.sendMessage(localeUtil.getinput("nber.err"));
                    return;
                }
                List<String> mapsList = (List<String>) Lists.getMapsList();
                int page = 1;
                if (0 < args.length) {
                    page = Integer.parseInt(args[0]);
                }
                int pages = Mathf.ceil((float) mapsList.size() / Config.MAXIMUM_SCREEN_DISPLAY);
                page--;
                if (page >= pages || 0 > page) {
                    player.sendMessage(localeUtil.getinput("maps.page.err", pages));
                    return;
                }
                final int triggerMode = 2;
                if (triggerMode == args.length) {
                    player.sendMessage(localeUtil.getinput("maps.page", (page + 1), pages));
                    for (int i = Config.MAXIMUM_SCREEN_DISPLAY * page; i < Math.min(Config.MAXIMUM_SCREEN_DISPLAY * (page + 1), mapsList.size()); i++) {
                        String[] data = mapsList.get(i).split("\\s+");
                        if (data[3].equalsIgnoreCase(args[1])) {
                            player.sendMessage(localeUtil.getinput("maps.mode.info", String.valueOf(i), data[0], data[1]));
                        }
                    }
                    return;
                }
                player.sendMessage(localeUtil.getinput("maps.page", (page + 1), pages));
                for (int i = Config.MAXIMUM_SCREEN_DISPLAY * page; i < Math.min(Config.MAXIMUM_SCREEN_DISPLAY * (page + 1), mapsList.size()); i++) {
                    String[] data = mapsList.get(i).split("\\s+");
                    player.sendMessage(localeUtil.getinput("maps.info", String.valueOf(i), data[0], data[1], data[2]));
                }
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
        });

        handler.<Player>register("vote", "<help> [parameter]", "4dV#-vote", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "vote";
            if (authorityControl(player, commid)) {
                if (!Vote.sted) {
                    player.sendMessage(localeUtil.getinput("vote.already_begun"));
                    return;
                }
                switch (args[0].toLowerCase()) {
                    case "help":
                        Call.onInfoToast(player.con, localeUtil.getinput("vote.help"), 40f);
                        break;
                    case "gameover":
                    case "skipwave":
                        Data.VOTE = new Vote(player, args[0]);
                        break;
                    case "kick":
                        if (1 < args.length) {
                            Data.VOTE = new Vote(player, args[0], args[1]);
                        } else {
                            player.sendMessage(localeUtil.getinput("args.err"));
                        }
                        break;
                    case "ff":
                        Data.VOTE = new Vote(player, args[0], player.getTeam());
                        break;
                    case "host":
                        if (1 < args.length) {
                            if (notisNumeric(args[1])) {
                                player.sendMessage(localeUtil.getinput("nber.err"));
                                return;
                            }
                            if (Lists.getMapsList().size() >= Integer.parseInt(args[1])) {
                                Data.VOTE = new Vote(player, args[0], args[1]);
                            } else {
                                player.sendMessage(localeUtil.getinput("vote.host.maps.err", args[1]));
                            }
                        } else {
                            player.sendMessage(localeUtil.getinput("args.err"));
                        }
                        break;
                    default:
                        player.sendMessage(localeUtil.getinput("vote.err", args[0]));
                        break;
                }
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
            }
        });

        handler.<Player>register("votekick", "<player>", "4dV#-votekick", (args, player) -> {
            final String commid = "votekick";
            if (authorityControl(player, commid)) {
                new Vote(player, "kick", args[0]);
            } else {
                player.sendMessage(Maps.getPlayerData(player.uuid).info.getinput("authority.no"));
            }
        });

        handler.<Player>register("ukey", "<key>", "4dV#-ukey", (args, player) -> {
            LocaleUtil localeUtil = Maps.getPlayerData(player.uuid).info;
            final String commid = "ukey";
            if (authorityControl(player, commid)) {
                if (isSqliteKey(args[0])) {
                    player.sendMessage(localeUtil.getinput("key.no"));
                } else {
                    PlayerData playerdata = Maps.getPlayerData(player.uuid);
                    java.util.Map<String, Object> data = getKey(args[0]);
                    long leftTime = Long.parseLong(data.get("Expire").toString());
                    if (leftTime < getLocalTimeFromU()) {
                        player.sendMessage(localeUtil.getinput("key.expire"));
                        newThredDb(() -> rmKey(data.get("KEY").toString()));
                        return;
                    }
                    int resultAuthority = Integer.parseInt(data.get("Authority").toString());
                    if (resultAuthority == playerdata.authority) {
                        player.sendMessage(localeUtil.getinput("key.use.no"));
                        // 暂时不支持同级 KEY
                    } else {
                        playerdata.authority = Integer.parseInt(data.get("Authority").toString());
                        if (Long.parseLong(data.get("Time").toString()) == 0) {
                            playerdata.authorityEffectiveTime = 0;
                        } else {
                            playerdata.authorityEffectiveTime = getLocalTimeFromU(Long.parseLong(data.get("Time").toString()) * 1000L + playerdata.gmt);
                        }
                        player.sendMessage(localeUtil.getinput("key.use.yes", playerdata.authority, longtoTime(playerdata.authorityEffectiveTime)));
                        final int sur = Integer.parseInt(data.get("Surplus").toString()) - 1;
                        if (sur == 0) {
                            newThredDb(() -> rmKey(data.get("KEY").toString()));
                        } else {
                            newThredDb(() -> saveKey(data.get("KEY").toString(), Integer.parseInt(data.get("Authority").toString()), Integer.parseInt(data.get("Total").toString()), sur, Long.parseLong(data.get("Time").toString()), Long.parseLong(data.get("Expire").toString())));
                        }
                        // OK
                    }
                }
            } else {
                player.sendMessage(localeUtil.getinput("authority.no"));
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