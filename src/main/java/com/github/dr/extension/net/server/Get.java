package com.github.dr.extension.net.server;

import arc.Core;
import com.alibaba.fastjson.JSON;
import com.github.dr.extension.data.db.Player;
import com.github.dr.extension.data.db.PlayerData;
import com.github.dr.extension.data.global.Data;
import com.github.dr.extension.util.encryption.Rsa;
import com.github.dr.extension.util.log.Log;
import mindustry.core.GameState.State;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static com.github.dr.extension.util.ExtractUtil.stringToUtf8;
import static com.github.dr.extension.util.IsUtil.isBlank;
import static com.github.dr.extension.util.IsUtil.notisBlank;
import static com.github.dr.extension.util.RandomUtil.generateStr;
import static com.github.dr.extension.util.log.Error.code;
import static com.github.dr.extension.util.log.Error.error;
import static mindustry.Vars.*;

/**
 * @author Dr
 * Web
 */
public class Get {

    private static final String GET = "/api/get/";
    private boolean ISKEY = false;

    protected void register(ServletContextHandler context) {
        context.addServlet(new ServletHolder(new Info()), GET + "info");
        context.addServlet(new ServletHolder(new Status()), GET + "status");
        context.addServlet(new ServletHolder(new Key()), GET + "key");
        //context.addServlet(new ServletHolder(new Login()), GET+"user/login");
    }


    /**
     * Util
     */
    private static PrintWriter getGzipWriter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String encodings = request.getHeader("Accept-Encoding");
        String flag = request.getParameter("disableGzip");
        boolean isGzipSupported = ((encodings != null) && (encodings.indexOf("gzip") != -1));
        boolean isGzipDisabled = ((flag != null) && (!"false".equalsIgnoreCase(flag)));
        if (isGzipSupported && !isGzipDisabled) {
            response.setHeader("Content-Encoding", "gzip");
            return new PrintWriter(new GZIPOutputStream(response.getOutputStream()));
        } else {
            return response.getWriter();
        }
    }

    private static void setHandler(HttpServletResponse response) {
        response.setHeader("Server", "Mindustry-Server-Web");
    }

    private static String getPostDate(HttpServletRequest request) {
        BufferedReader in = null;
        StringBuffer result = new StringBuffer();
        String line = null;
        try {
            request.setCharacterEncoding("UTF-8");
            String contentEncoding = request.getHeader("Content-Encoding");
            if (null != contentEncoding && contentEncoding.indexOf("gzip") != -1) {
                GZIPInputStream gZIPInputStream = new GZIPInputStream(request.getInputStream());
                in = new BufferedReader(new InputStreamReader(gZIPInputStream));
                while ((line = in.readLine()) != null) {
                    result.append(new String(line.getBytes("ISO-8859-1"),Data.UTF_8));
                }
            } else {
                in = new BufferedReader(new InputStreamReader(request.getInputStream(),Data.UTF_8));
                while ((line = in.readLine()) != null) {
                    result.append("\n"+line);
                }
            }
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    /**
     * Core
     */
    private class Info extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = getGzipWriter(request, response);
            setHandler(response);
            String user = request.getParameter("user");
            String info = generateStr(10);
            PlayerData data = new PlayerData(info, info, 0);
            Player.getSqlite(data, user);
            Map<String, Object> result = new HashMap<String, Object>(4);
            result.put("state", code("SUCCESS"));
            result.put("result", Base64.getEncoder().encodeToString(JSON.toJSONString(data).getBytes(Data.UTF_8)));
            out.println(stringToUtf8(JSON.toJSONString(result)));
            out.close();
        }
    }

    private class Status extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = getGzipWriter(request, response);
            setHandler(response);
            Map<String, Object> map = new HashMap<>(8);
            Map<String, Object> result = new HashMap<String, Object>(4);
            if (state.is(State.playing)) {
                map.put("fps", Core.graphics.getFramesPerSecond());
                map.put("mob", Core.app.getJavaHeap() / 1024 / 1024);
                map.put("player", playerGroup.size());
                map.put("map", world.getMap().name());
                result.put("state", code("SUCCESS"));
                result.put("result", Base64.getEncoder().encodeToString(JSON.toJSONString(map).getBytes(Data.UTF_8)));
            } else {
                result.put("state", code("SERVER_CLOSE"));
            }
            out.println(stringToUtf8(JSON.toJSONString(result)));
            out.close();
        }
    }

    /**
     * 认证思路
     * 向服务器发送一个Key -> 服务器返回一个RSA-Pub -> 加密密文 发送
     * 缺陷: 第一次验证依然是不安全的 且密码可被窃取
     * 而解决方法为 第一次采取内部证书(或第一次以自定义密钥与时间生成TOPT) 则可提高安全性 抛弃后续获取key的全部请求 只接收密钥更换请求
     * 我认为暂时用不上 本地可以用明码 问题不大
     */
    private class Key extends HttpServlet {
        @Override
        // GET 当然是不够的 只能切为POST (也好压缩啊)
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = getGzipWriter(request, response);
            String data = getPostDate(request);
            setHandler(response);
            Map<String, Object> result = new HashMap<String, Object>(4);
            if (notisBlank(data)) {
                if (isBlank(Data.PRIVATEKEY)) {
                    if (true) {

                    }
                    try {
                        KeyPair keyPair = new Rsa().buildKeyPair();
                        result.put("state", code("SUCCESS"));
                    } catch (NoSuchAlgorithmException e) {
                        Log.error(error("UNSUPPORTED_ENCRYPTION"),e);
                    }
                } else {
                    
                }

            } else {
                result.put("state", code("INCOMPLETE_PARAMETERS"));
            }
            result.put("result", null);
            out.println(stringToUtf8(JSON.toJSONString(result)));
            out.close();
        }
    }

    private class Bind extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = getGzipWriter(request, response);
            setHandler(response);
            Map<String, Object> result = new HashMap<String, Object>(8);
            result.put("state", code("SUCCESS"));
            result.put("result", null);
            out.println(stringToUtf8(JSON.toJSONString(result)));
            out.close();
        }
    }
}