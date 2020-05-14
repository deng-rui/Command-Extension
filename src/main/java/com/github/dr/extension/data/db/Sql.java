package com.github.dr.extension.data.db;

import com.github.dr.extension.util.log.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author Dr
 */
public class Sql {
      
    public static void initSqlite() {
        try {
            String sql;
            //Connection c = DriverManager.getConnection("jdbc:sqlite:"+FileUtil.File(Data.PLUGIN_DATA_PATH).getPath("Data.db"));
            Connection c = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3308/Data","root","dr");
            Statement stmt = c.createStatement();
            // 时间可以改成BUGINT
            sql = "CREATE TABLE PlayerData (" +
                  "UUID                     VARCHAR(40),"+
                  "User                     VARCHAR(20)             NOT NULL,"+
                  "IP                       BIGINT,"+
                  "GMT                      INT,"+
                  "Country                  VARCHAR(10),"+
                  "Time_format              TINYINT(1),"+
                  "Language                 VARCHAR(5),"+
                  "LastLogin                BIGINT,"+
                  //
                  "Buildcount               INTEGER,"+
                  "Dismantledcount          INTEGER,"+
                  "Cumulative_build         INTEGER,"+
                  "Pipe_build               INTEGER,"+
                  //玩家普通信息
                  "Kickcount                INTEGER,"+
                  //被踢次数
                  "Translate                TINYINT(1),"+
                  //翻译权限
                  "Level                    INTEGER,"+
                  "Exp                      BIGINT,"+
                  "Reqexp                   BIGINT,"+
                  "Reqtotalexp              BIGINT,"+
                  //等级
                  "Playtime                 BIGINT,"+
                  //游戏时长
                  "Pvpwincount              INTEGER,"+
                  "Pvplosecount             INTEGER,"+
                  //胜利 输数
                  "Authority                TINYINT,"+
                  "Authority_effective_time BIGINT,"+
                  //权限
                  "Lastchat                 BIGINT,"+
                  //最后聊天时间 聊天计数
                  "Deadcount                INTEGER,"+
                  "Killcount                INTEGER,"+
                  "Joincount                INTEGER,"+
                  "Breakcount               INTEGER,"+
                  "CONSTRAINT User UNIQUE (User))"; 
                  //玩家死亡 击杀 加入 退出次数
                  //TEST阶段 仅在GA-PVP使用
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE PlayerPrivate (" +
                  "User                     VARCHAR(20)             NOT NULL,"+
                  "Mail                     CHAR(50),"+
                  "PasswordHash             TEXT,"+
                  "CSPRNG                   TEXT,"+
                  "CONSTRAINT User UNIQUE (User))"; 
            //Cryptographically Secure Pseudo-Random Number Generator
            //安全系列
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE PlayerOnline (" +
                  "User                     VARCHAR(20)             NOT NULL,"+
                  "UUID                     VARCHAR(40),"+
                  "ServerLastID             VARCHAR(30),"+
                  "Online                   TINYINT(1),"+
                  "CONSTRAINT OnlineData UNIQUE (User,UUID))";
            //安全系列3
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE KeyData (" +
                  "KeyCode                  VARCHAR(20)             NOT NULL,"+
                  "Authority                TINYINT,"+
                  "Total                    TINYINT,"+
                  "Surplus                  TINYINT,"+
                  "Time                     BIGINT,"+
                  "Expire                   BIGINT,"+
                  "CONSTRAINT KeyCode UNIQUE (KeyCode))";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
            Log.info("WSQL");
        } catch (Exception e ) {
            Log.fatal("Create SQL",e);
        }
    }

}