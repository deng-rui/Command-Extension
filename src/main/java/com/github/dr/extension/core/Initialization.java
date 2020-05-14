package com.github.dr.extension.core;

import arc.ApplicationListener;
import arc.Core;
import com.github.dr.extension.core.ex.Threads;
import com.github.dr.extension.data.global.Config;
import com.github.dr.extension.data.global.Data;
import com.github.dr.extension.data.global.Lists;
import com.github.dr.extension.data.global.Maps;
import com.github.dr.extension.net.Net;
import com.github.dr.extension.util.LocaleUtil;
import com.github.dr.extension.util.file.FileUtil;
import com.github.dr.extension.util.log.Log;
import mindustry.maps.Map;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.github.dr.extension.data.db.Sql.*;
import static com.github.dr.extension.dependent.Librarydependency.importLib;
import static com.github.dr.extension.util.file.LoadConfig.loadstring;
import static mindustry.Vars.maps;

public class Initialization {

    public void startInitialization() {
        // 配置文件初始化
        config();
        //Resource();
        //IsCN();

        // 初始化计数线程
        new Threads().initialization();

        // 新线程 初始化数据 (WARN: SQL不能使用新线程)
        //Threads.newThredSe(() -> );
        sql();
        // 新线程 初始化权限list
        Threads.newThredSe(() -> playerPrivilegeClassification());

        // 加载语言
        Maps.setLocale("zh_CN", new LocaleUtil("zh_CN"));
        Maps.setLocale("zh_HK", new LocaleUtil("zh_HK"));
        Maps.setLocale("zh_MO", new LocaleUtil("zh_MO"));
        Maps.setLocale("zh_TW", new LocaleUtil("zh_TW"));
        Maps.setLocale("ru_RU", new LocaleUtil("ru_RU"));
        Maps.setLocale("en_US", new LocaleUtil("en_US"));
    }

    public void overrideInitialization() {
        coverGameover();
    }


    public void reLoadConfig() {
        Config.laodConfig();
        playerPrivilegeClassification();
    }


    private void coverGameover() {
        // 覆盖掉自带的Gameover 使用自己的 便于自动更换模式
        try {
            ApplicationListener sr = Core.app.getListeners().find(e -> "ServerControl".equalsIgnoreCase(e.getClass().getSimpleName()));
            Field field = sr.getClass().getDeclaredField("inExtraRound");
            field.setAccessible(true);
            field.setBoolean(sr, true);
            field.setAccessible(false);
        } catch (Exception e) {
            Log.fatal("Cover Gameover error", e);
        }
    }


    private void sql() {
        //importLib("org.xerial", "sqlite-jdbc", "3.30.1", Data.PLUGIN_LIB_PATH, "org.sqlite.JDBC");
        importLib("org.mariadb.jdbc", "mariadb-java-client", "2.6.0", Data.PLUGIN_LIB_PATH, "org.mariadb.jdbc.Driver");
        if (!Core.settings.getDataDirectory().child("mods/GA/Data.db").exists()) {
            initSqlite();
        }
        /**
         * 加载全局SQL Conntion
         */
        new Data().initialization();
    }


    private void config() {
        try {
            if (!FileUtil.File(Data.PLUGIN_DATA_PATH).toPath("/Config.ini").exists()) {
                String data = (String) FileUtil.readfile(false, new InputStreamReader(Initialization.class.getResourceAsStream("/Config.ini"), "UTF-8"));
                FileUtil.writefile(data, false);
                Log.info("Defect : Start creating write external Config File", Data.PLUGIN_DATA_PATH + "/Config.ini");
            }
        } catch (UnsupportedEncodingException e) {
            Log.fatal("File write error", e);
        }
        /**
         * 加载配置文件
         */
        Config.laodConfig();
    }

    // CP出jar特定文件至硬盘
    // 下个版本 弃用 -X
    // 无法便于更新语言文件
    private void resource() {
        try {
            List file = (List) FileUtil.readfile(true, new InputStreamReader(Initialization.class.getResourceAsStream("/other/FileList.txt"), "UTF-8"));
            for (int i = 0, len = file.size(); i < len; i++) {
                if (!FileUtil.File(Data.PLUGIN_RESOURCES_PATH + (String) file.get(i)).exists()) {
                    //IPR必须加上/
                    String a = (String) FileUtil.readfile(false, new InputStreamReader(Initialization.class.getResourceAsStream((String) file.get(i)), "UTF-8"));
                    FileUtil.writefile(a, false);
                    Log.info("Defect : Start creating write external resource File", Data.PLUGIN_RESOURCES_PATH + (String) file.get(i));
                }
            }
        } catch (UnsupportedEncodingException e) {
            Log.fatal("File write error", e);
        }
    }

    public void mapList() {
        Lists.emptyMapsList();
        if (!maps.all().isEmpty()) {
            for (Map map : maps.all()) {
                // 只会加载自定义地图
                if (map.custom) {
                    switch (String.valueOf(map.file.name().charAt(0))) {
                        case "P":
                        case "p":
                            Lists.addMapsList(map.name().replace(' ', '_') + " " + map.width + "x" + map.height + " pvp" + " P");
                            break;
                        case "S":
                        case "s":
                            Lists.addMapsList(map.name().replace(' ', '_') + " " + map.width + "x" + map.height + " survival" + " S");
                            break;
                        case "A":
                        case "a":
                            Lists.addMapsList(map.name().replace(' ', '_') + " " + map.width + "x" + map.height + " attack" + " A");
                            break;
                        default:
                            break;
                    }
                }
            }
        } else {
            Lists.addMapsList("The map is empty");
        }
    }

    private void isNetwork() {
        Config.SERVER_NETWORKING = Net.isConnect();
    }

    private void isCn() {

    }

    // RG-1
    private void playerPrivilegeClassification() {
        String[] tempstring = loadstring("Privilege_Level").split(",");
        int[] tempint = new int[tempstring.length];
        for (int i = 0, len = tempstring.length; i < len; i++) {
            tempint[i] = Integer.parseInt(tempstring[i]);
        }
        int[] temp = selectionSort(tempint);
        if (Config.PERMISSION_PASSING) {
            for (int i = 0, len = temp.length; i < len; i++) {
                final String data = loadstring("Privilege." + temp[i]);
                if (data == null) {
                    continue;
                }
                if (i <= 1) {
                    Maps.setPowerData(i, Arrays.asList(data.split(",")));
                    continue;
                }
                List<String> power = new ArrayList<String>(Arrays.asList(data.split(",")));
                power.addAll(Maps.getPowerData(temp[i - 1]));
                List<String> filter = new ArrayList<String>();
                filter.addAll(new HashSet<String>(power));
                Maps.setPowerData(temp[i], filter);
            }
        } else {
            for (int i = 0, len = temp.length; i < len; i++) {
                final String data = loadstring("Privilege." + temp[i]);
                if (data == null) {
                    continue;
                }
                Maps.setPowerData(temp[i], Arrays.asList(data.split(",")));
            }
        }
    }

    private int[] selectionSort(int[] array) {
        if (array.length == 0) {
            return array;
        }
        for (int i = 0, len = array.length; i < len; i++) {
            int minIndex = i;
            for (int j = i, len1 = array.length; j < len1; j++) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }
            int temp = array[minIndex];
            array[minIndex] = array[i];
            array[i] = temp;
        }
        return array;
    }
}