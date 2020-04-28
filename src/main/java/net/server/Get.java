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

import static extension.util.log.Error.Code;
import static extension.util.RandomUtil.generateStr;

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
		context.addServlet(new ServletHolder(new Key()), "/api/get/key");
	}

	private static boolean isGzipSupported(HttpServletRequest request) {
		String encodings = request.getHeader("Accept-Encoding");
		return((encodings != null) &&(encodings.indexOf("gzip") != -1));
	}

	private static boolean isGzipDisabled(HttpServletRequest request) {
		String flag = request.getParameter("disableGzip");
		return((flag != null) && (!flag.equalsIgnoreCase("false")));
	}

	protected static PrintWriter getGzipWriter(HttpServletResponse response) throws IOException {
		if (GzipUtilities.isGzipSupported(request) && !GzipUtilities.isGzipDisabled(request)) {
			response.setHeader("Content-Encoding", "gzip");
			return new PrintWriter(new GZIPOutputStream(response.getOutputStream()));
		} else 
			return response.getWriter();
	}

	protected static void setHandler(HttpServletResponse response) {
		response.setHeader("Server", "Mdt-Server-Web");
	}
}

// result Json
class Info extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = Get.getGzipWriter(response);
		Get.setHandler(response);
		String user = request.getParameter("user");
		String info = generateStr(10);
		PlayerData data = new PlayerData(info,info,0);
		Player.getSQLite(data,user);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state",Code("SUCCESS"));
		result.put("result",Base64.getEncoder().encodeToString(JSON.toJSONString(data).getBytes("utf-8")));
		out.println(result);
		out.close();
	}
}

// result Json
class Status extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = Get.getGzipWriter(response);
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
		} else 
			out.println("state",Code("SERVER_CLOSE"));
		out.println(result);
		out.close();
	}
}

class Key extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = Get.getGzipWriter(response);
		Get.setHandler(response);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state",Code("SUCCESS"));
		out.println(result);
		out.close();
	}
}


// class Info {
// 	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
// 	}
// }