package extension.net.server;

import java.io.*;
import java.util.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import javax.servlet.http.HttpServletRequest;

public class Start {
	public Start() {
		try {
			Server server = new Server(8080);   
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS); 
			context.setErrorHandler(new CustomErrorHandler()); 
			context.setContextPath("/");   
			server.setHandler(context); 
			new Get().register(context);
			new Post().register(context);
			server.start(); 
			server.join(); 
		} catch (Exception e) {
		}
	}

	class CustomErrorHandler extends ErrorHandler {
		@Override
		protected void writeErrorPage(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks) throws IOException {
			// 不给你返回 :P
		}
	}
}