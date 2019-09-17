package extension;

import java.sql.*;
import io.anuke.arc.Core;
import java.io.*;
import java.net.*;
import java.util.*;

public class SQLiteJDBC
{
String SQ="jdbc:sqlite:"+Core.settings.getDataDirectory().child("plugins/test.db");
  public static void main()
  {
    Connection c = null;
    Statement stmt = null;
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection(SQ);
    } catch ( Exception e ) {
      System.exit(0);
    }
    System.out.println("Opened database successfully");
  }

  public static void main1()
  {
    Connection c = null;
    Statement stmt = null;
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection(SQ);
      stmt = c.createStatement();
      System.out.println("Opened database successfully");
      String sql = "CREATE TABLE PLAYDATE " +
                   "(UUID TEXT PRIMARY KEY     NOT NULL," +
                   " NAME              TEXT    NOT NULL," + 
                   " Country           TEXT    NOT NULL," + 
                   " BlockPlace        INT     NOT NULL," + 
                   " BlockBreak        INT     NOT NULL," + 
                   " KillUnits         INT     NOT NULL," + 
                   " suicideCount      INT     NOT NULL," + 
                   " FirstJoin         TEXT    NOT NULL," + 
                   " LastJoin          TEXT    NOT NULL," + 
                   " Playtime          TEXT    NOT NULL)"; 
      stmt.executeUpdate(sql);
      /*
      sql = "INSERT INTO PLAYDATE (UUID,NAME,Country,Block place,Block break,Kill units,suicide count,First join,Last join,Playtime) " +
                   "VALUES (ACFDNDSSJFNV=,'Dr','CHINA',0,0,5,0,'2019.2.2','2019.1.1','nmsl' );"; 
      stmt.executeUpdate(sql);
      sql = "INSERT INTO PLAYDATE (UUID,NAME,Country,Block place,Block break,Kill units,suicide count,First join,Last join,Playtime) " +
                   "VALUES (ACFDNDSDJSDGJV=,'Sr','CHIDFSDA',57,0,5,0,'2019.2.2','2019.1.1','nmsl' );"; 
      stmt.executeUpdate(sql);
      */
      stmt.close();
      c.close();
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    System.out.println("Table created successfully");
  }

  public static String main2(String uuid)
  {
    Connection c = null;
    Statement stmt = null;
    String name=null;
      try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection(SQ);
      c.setAutoCommit(false);
      System.out.println("Opened database successfully");

      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery( "select * from accuont where VIP="+uuid);
      //while ( rs.next() ) {
         int Blockplace = rs.getInt("Block place");
         name = rs.getString("name");
         //String  name = rs.getString("name");
         //int age  = rs.getInt("age");
      // String  address = rs.getString("address");
         //float salary = rs.getFloat("salary");
         //System.out.println( "ID = " + id );
         
      //}
      rs.close();
      stmt.close();
      c.close();
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    return name;
  }



}