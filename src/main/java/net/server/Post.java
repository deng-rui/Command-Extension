
package extension.net.server;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import extension.net.HttpRequest;

import static extension.util.log.Error.Code;
import static extension.util.RandomUtil.generateStr;

import com.alibaba.fastjson.JSON;

public class Post {
	protected void register(ServletContextHandler context) {
		context.addServlet(new ServletHolder(new Bind()), "/api/post/bind");
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

class Bind extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = Get.getGzipWriter(request,response);
		Get.setHandler(response);
		Map<String,Object> result = new HashMap<>();
		result.put("state",Code("SUCCESS"));
		result.put("result",null);
		out.println(result);
		out.close();
	}
}
