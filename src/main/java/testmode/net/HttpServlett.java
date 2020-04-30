package extension.testmode.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.*;
import java.nio.charset.StandardCharsets;

import arc.Core;
import static mindustry.Vars.playerGroup;
import static mindustry.Vars.world;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet; 
import org.eclipse.jetty.server.*; 
import org.eclipse.jetty.servlet.ServletContextHandler; 
import org.eclipse.jetty.servlet.ServletHolder; 
import org.eclipse.jetty.server.handler.*;

import extension.data.db.Player;
import extension.data.db.PlayerData;
import extension.data.global.Maps;

import com.alibaba.fastjson.JSON;

import static extension.util.alone.Password.Passwdverify;
import static extension.util.RandomUtil.generateStr;


public class HttpServlett {

	public void start() {
		try {
			Server server = new Server(8080);   
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS); 
			context.setErrorHandler(new CustomErrorHandler()); 
			context.setContextPath("/");   
			server.setHandler(context); 
			context.addServlet(new ServletHolder(new A1()), "/api/get"); 
			context.addServlet(new ServletHolder(new A2()), "/api/set"); 
			context.addServlet(new ServletHolder(new B1("Hello FirstServlet!")), "/test");   
			server.start(); 
			server.join(); 
		} catch (Exception e) {
		}
	}  
}

class A1 extends HttpServlet {    
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		String user = request.getParameter("user");
		String passwd = request.getParameter("passwd");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		PlayerData data = null;
		String text = null;
		switch(type){
			case "info" :
				//正常
				String info = generateStr(10);
				data = new PlayerData(info,info,0);
				Player.getSQLite(data,user);
				text = Base64.getEncoder().encodeToString(JSON.toJSONString(data).getBytes("utf-8"));
				response.getWriter().println(text);
				break;
			case "bind" :
				if(Player.isSQLite_User(user)) {
					response.getWriter().println("2");
				} else {
					String bind = generateStr(10);
					data = new PlayerData(bind,bind,0);
					Player.getSQLite(data,user);
					try {
						if(!Passwdverify(passwd,data.PasswordHash,data.CSPRNG)) {
						response.getWriter().println("1");
						return;
						}
					} catch (Exception e) {
						response.getWriter().println("3");
						return;
					}
					response.getWriter().println("0");
				}
				break;
			case "status" :
				Map<String,Object> map = new HashMap<>();
			    map.put("fps", Core.graphics.getFramesPerSecond());
			    map.put("mob", Core.app.getJavaHeap()/1024/1024);
			    map.put("player", playerGroup.size());
			    map.put("map", world.getMap().name());
				text = Base64.getEncoder().encodeToString(JSON.toJSONString(map).getBytes("utf-8"));
				response.getWriter().println(text);
				break;
			default :
				//event.getGroup().sendMessage(CustomLoad("bind.err"));
				//我不知道
				break;
		}
	} 
}

class A2 extends HttpServlet {    
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name"); 
		String password = request.getParameter("password");  
		response.setCharacterEncoding("UTF-8"); 
		response.setContentType("text/html"); 
		response.setStatus(HttpServletResponse.SC_OK); 
		response.getWriter().println("<h1>" + "hi" + "</h1>"); 
		response.getWriter().println("测试中文</br>session=" + request.getSession(true).getId()+"</br>");   
	} 
}

class B1 extends HttpServlet { 
		private String msg = "Hello World!";   
	 
		public B1() { 
		}   
	 
		public B1(String msg) { 
			this.msg = msg; 
		}   
	 
		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			String name = request.getParameter("name"); 
			String password = request.getParameter("password");  
	 
			response.setCharacterEncoding("UTF-8"); 
			response.setContentType("text/html"); 
			response.setStatus(HttpServletResponse.SC_OK); 
			response.getWriter().println("<h1>" + msg + "</h1>"); 
			response.getWriter().println("测试中文</br>session=" + request.getSession(true).getId()+"</br>"); 
			if(name!=null) {
                response.getWriter().println("名字："+name+"</br>");
            }
	 
			if(password!=null) {
                response.getWriter().println(" 密码："+password+"</br>");
            }
	 
		} 
}

class CustomErrorHandler extends ErrorHandler {
	   @Override
	   protected void writeErrorPage(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks) throws IOException {
		writer.write("404");
	   }
}