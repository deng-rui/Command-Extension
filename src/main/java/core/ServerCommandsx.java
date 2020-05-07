package extension.core;

import arc.Core;
import arc.Events;
import arc.util.CommandHandler;
import extension.core.ex.Threads;
import extension.data.db.Player;
import extension.data.db.PlayerData;
import extension.data.global.Maps;
import mindustry.game.EventType.GameOverEvent;
import mindustry.game.Team;

import java.util.List;
import java.util.Map;

import static arc.util.Log.info;
import static extension.core.ex.Extend.secToTime;
import static extension.util.DateUtil.getLocalTimeFromU;
import static extension.util.DateUtil.longtoTime;
import static extension.util.IsUtil.notisNumeric;
import static extension.util.RandomUtil.generateStr;
import static mindustry.Vars.maps;
import static mindustry.Vars.net;


public class ServerCommandsx {
    public void register(CommandHandler handler) {
        handler.removeCommand("exit");
        handler.removeCommand("gameover");
        handler.removeCommand("reloadmaps");

        handler.register("testinfo", "[GA]", "GA TEST", (arg) -> {
            //info("{0}", getinput(arg[0]));
            System.out.println((arg.length > 0) ? arg[0] : null);
        });

        handler.register("reloadmaps", "reload maps", (arg) -> {
            int beforeMaps = maps.all().size;
            maps.reload();
            if (maps.all().size > beforeMaps) {
                info("&lc{0}&ly new map(s) found and reloaded.", maps.all().size - beforeMaps);
            } else {
                info("&lyMaps reloaded.");
            }
            new extension.core.Initialization().mapList();
        });

        handler.register("gameover", "Force a game over", (arg) -> {
            info("&lyCore destroyed.");
            Events.fire(new GameOverEvent(Team.crux));
        });

        handler.register("reloadconfig", "reload Command-Extension Config.ini", (arg) -> {
            info("&lyReLoad Config.ini End.");
            new extension.core.Initialization().reLoadConfig();
        });

        handler.register("toadmin", "<uuid> <id>", "reload Command-Extension Config.ini", (arg) -> {
            PlayerData playerdata = Maps.getPlayerData(arg[0]);
            if (playerdata != null) {
                playerdata.authority = Integer.parseInt(arg[1]);
            }
            playerdata.translate = true;
        });

        handler.register("exit", "Exit the server application", (arg) -> {
            info("Shutting down server.");
            net.dispose();
            Threads.close();
            Core.app.exit();
        });

        handler.register("newkey", "<length> <authority> <Available_time(min)> <Expiration_date(min)> [Total]", "Add new key", (arg) -> {
            if (notisNumeric(arg[0])) {
                info("Invalid length");
            } else if (notisNumeric(arg[1])) {
                info("Invalid permission");
            } else {
                Player.addKey(generateStr(Integer.parseInt(arg[0])), Integer.parseInt(arg[1]), Long.parseLong(arg[2]) * 60, getLocalTimeFromU(Long.parseLong(arg[3]) * 60000L), arg.length > 4 ? Integer.parseInt(arg[4]) : 1);
            }
        });

        handler.register("keys", "List all keys", (arg) -> {
            List<Map<String, Object>> data = Player.getKey();
            info("KEY List:");
            for (Map<String, Object> map : data) {
                info("Authority: {0} ,KEY: {1} \n Surplus/Total : {2}/{3} \nTime: {4} Expire(UTC): {5}", map.get("Authority"), map.get("Key"), map.get("Surplus"), map.get("Total"), secToTime((long) map.get("Time")), longtoTime((long) map.get("Expire")));
            }
        });

        handler.register("rmkeys", "Rm all keys", (arg) -> {
            Player.rmKey();
            info("Delete all key");
        });

        handler.register("rmkey", "<key>", "rm key", (arg) -> {
            if (!Player.isSqliteKey(arg[0])) {
                Player.rmKey(arg[0]);
            } else {
                info("Invalid key, key:{0}", arg[0]);
            }
        });
    }
}