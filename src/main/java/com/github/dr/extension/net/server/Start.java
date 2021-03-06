package com.github.dr.extension.net.server;

import com.github.dr.extension.data.global.Config;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class Start {

	private final Server server = new Server();

	public Start() {
		try {
			ServletContextHandler serverPath = new ServletContextHandler(ServletContextHandler.SESSIONS);

			/**
			 * 设置根目录
			 * 便于监听/*
			 * 以覆盖默认ERROR WEB
			 */
			serverPath.setContextPath("/");
			serverPath.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
			serverPath.setErrorHandler(new CustomErrorHandler()); 
			
			/**
			 * 监听/api/
			 */
			new com.github.dr.extension.net.server.Get().register(serverPath);
			new com.github.dr.extension.net.server.Post().register(serverPath);

			/**
			 * 设置WEB目录
			 * 便于监听/web/
			 */
	        ContextHandler context1 = new ContextHandler();
	        context1.setContextPath("/web/*");
	        File dir1 = new File("/mnt/l/web");
	        context1.setBaseResource(Resource.newResource(dir1));
	        context1.setHandler(new ResourceHandler());

	        // 绑定两个资源句柄
	        ContextHandlerCollection contexts = new ContextHandlerCollection();
	        contexts.setHandlers(new Handler[] {
	        	serverPath,
	        	context1
	        });

			/**
			 * SSL
			 */
			SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
			sslContextFactory.setKeyStorePath("/mnt/l/web/web-mindustry-top.jks");
			sslContextFactory.setKeyStorePassword(Config.SSL_PASSWD);
			sslContextFactory.setKeyManagerPassword(Config.SSL_PASSWD);
			/**
			 * SSL-HTTP 1.1 不想支持2(需要新依赖 org.eclipse.jetty.http2)
			 * 启用重定向 302
			 */
			HttpConfiguration httpConf = new HttpConfiguration();
			httpConf.setSecurePort(8443);
			httpConf.setSecureScheme("https");
			HttpConfiguration httpsConf = new HttpConfiguration(httpConf);
			httpsConf.addCustomizer(new SecureRequestCustomizer());
			ServerConnector serverConnector = new ServerConnector(server,new SslConnectionFactory(sslContextFactory, "http/1.1"),new HttpConnectionFactory(httpsConf));
			//ServerConnector serverConnector = new ServerConnector(server, prepareSsl(alpn),alpn, http2ConnectionFactory, httpConnectionFactory);
			serverConnector.setPort(8443);
			serverConnector.setReuseAddress(true);

			server.addConnector(serverConnector);
		    server.setHandler(contexts);
			server.start();
			//✓
			server.join();
		} catch (Exception e) {
		}
	}

	private class CustomErrorHandler extends ErrorPageErrorHandler {
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
			response.setHeader("Server", "Mindustry-Server-Web");
			response.getWriter().println(String.valueOf(response.getStatus()));
			response.getWriter().println(request.getRequestURI());
			response.getWriter().println(request.getServletPath());
		}
	}
}
