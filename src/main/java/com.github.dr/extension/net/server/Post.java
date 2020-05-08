
package com.github.dr.extension.net.server;

import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

public class Post {

	private static final String POST = "/api/get/";
	private static final String WEB = "/api/get/";

	protected void register(ServletContextHandler context) {
		
	}

	protected String getBodyDate(HttpServletRequest request) {
		BufferedReader br = null;
		StringBuffer buf = new StringBuffer();
		try {
			request.setCharacterEncoding("UTF-8");
			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
                buf.append(line);
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return buf.toString();
	}

	private static boolean isGzipSupported(HttpServletRequest request) {
		String encodings = request.getHeader("Accept-Encoding");
		return((encodings != null) &&(encodings.indexOf("gzip") != -1));
	}

  
	private static boolean isGzipDisabled(HttpServletRequest request) {
		String flag = request.getParameter("disableGzip");
		return((flag != null) && (!"false".equalsIgnoreCase(flag)));
	}


	private static PrintWriter getGzipWriter(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (isGzipSupported(request) && !isGzipDisabled(request)) {
			response.setHeader("Content-Encoding", "gzip");
			return new PrintWriter(new GZIPOutputStream(response.getOutputStream()));
		} else {
            return response.getWriter();
        }
	}

	private static void setHandler(HttpServletResponse response) {
		response.setHeader("Server", "Mdt-Server-Web");
	}

}
