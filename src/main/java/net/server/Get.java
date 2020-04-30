package extension.net.server;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import java.security.*;

import arc.Core;
import mindustry.core.GameState.State;
import static mindustry.Vars.playerGroup;
import static mindustry.Vars.state;
import static mindustry.Vars.world;

import extension.data.db.Player;
import extension.data.db.PlayerData;
import extension.data.global.CacheData;
import extension.data.global.Maps;
import extension.net.HttpRequest;
import extension.util.log.Log;

import static extension.util.log.Error.Code;
import static extension.util.RandomUtil.generateStr;
import static extension.util.IsUtil.isBlank;

/*
 *Web 
 */
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet; 

import com.alibaba.fastjson.JSON;

public class Get {
	protected void register(ServletContextHandler context) {
		context.addServlet(new ServletHolder(new Info()), "/api/get/info");
		context.addServlet(new ServletHolder(new Status()), "/api/get/status");
		//context.addServlet(new ServletHolder(new Key()), "/api/get/key");
	}

	private static boolean isGzipSupported(HttpServletRequest request) {
		String encodings = request.getHeader("Accept-Encoding");
		return((encodings != null) &&(encodings.indexOf("gzip") != -1));
	}

	private static boolean isGzipDisabled(HttpServletRequest request) {
		String flag = request.getParameter("disableGzip");
		return((flag != null) && (!"false".equalsIgnoreCase(flag)));
	}

	protected static PrintWriter getGzipWriter(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (isGzipSupported(request) && !isGzipDisabled(request)) {
			response.setHeader("Content-Encoding", "gzip");
			return new PrintWriter(new GZIPOutputStream(response.getOutputStream()));
		} else {
            return response.getWriter();
        }
	}

	protected static void setHandler(HttpServletResponse response) {
		response.setHeader("Server", "Mdt-Server-Web");
	}
}

// result Json
class Info extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = Get.getGzipWriter(request,response);
		Get.setHandler(response);
		String user = request.getParameter("user");
		String info = generateStr(10);
		PlayerData data = new PlayerData(info,info,0);
		Player.getSQLite(data,user);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state",Code("SUCCESS"));
		result.put("result",Base64.getEncoder().encodeToString(JSON.toJSONString(data).getBytes("utf-8")));
		out.println(JSON.toJSONString(result));
		out.close();
	}
}

// result Json
class Status extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = Get.getGzipWriter(request,response);
		Get.setHandler(response);
		Map<String,Object> map = new HashMap<>();
		Map<String, Object> result = new HashMap<String, Object>();
		if(state.is(State.playing)) {
			map.put("fps", Core.graphics.getFramesPerSecond());
			map.put("mob", Core.app.getJavaHeap()/1024/1024);
			map.put("player", playerGroup.size());
			map.put("map", world.getMap().name());
			result.put("state",Code("SUCCESS"));
			result.put("result",Base64.getEncoder().encodeToString(JSON.toJSONString(map).getBytes("utf-8")));
		} else {
            result.put("state",Code("SERVER_CLOSE"));
        }
		out.println(JSON.toJSONString(result));
		out.close();
	}
}

/*
	 * 认证思路 仿照 (SSL)
	 * 向服务器发送一个保持Key -> 服务器->>Bot返回一个RSA-Pub -> 携带Key+加密文 发送
	 * 我认为暂时用不上 本地可以用明码 问题不大
	 */
// 暂未开放
class Key extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = Get.getGzipWriter(request,response);
		Get.setHandler(response);
		Map<String, Object> result = new HashMap<String, Object>();
		String botuuid = request.getParameter("botuuid");
		if(isBlank(botuuid)) {
			result.put("state",Code("INCOMPLETE_PARAMETERS"));
			out.println(result);
			out.close();
			return;
		}
		if(!CacheData.isRSACache(botuuid)) 
			//CacheData.addRSACache(botuuid);
        {
            result.put("state",Code("SUCCESS"));
        }
		result.put("result",CacheData.getRSACache_Puky(botuuid));
		out.println(result);
		out.close();
	}
}


// class Info {
// 	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
// 	}
// }