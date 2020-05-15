package com.github.dr.extension.net.server;

import arc.Core;
import com.alibaba.fastjson.JSON;
import com.github.dr.extension.data.db.PlayerData;
import com.github.dr.extension.data.global.Data;
import com.github.dr.extension.util.encryption.Base64;
import com.github.dr.extension.util.encryption.Rsa;
import com.github.dr.extension.util.log.Log;
import mindustry.core.GameState.State;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
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
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static com.github.dr.extension.util.ExtractUtil.stringToUtf8;
import static com.github.dr.extension.util.IsUtil.*;
import static com.github.dr.extension.util.RandomUtil.generateStr;
import static com.github.dr.extension.util.encryption.Base64.isBase64;
import static com.github.dr.extension.util.encryption.Topt.verifyTotpFlexibility;
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
        boolean isGzipSupported = ((encodings != null) && (encodings.contains("gzip")));
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
            /**
             * 很遗憾 JETTY不支持GZIP流 普通的只支持UTF-8解码
             * 我会考虑采取GzipHandler的 (没希望)
             * RSA加密的内容还没16K大
             */
            in = new BufferedReader(new InputStreamReader(request.getInputStream(),Data.UTF_8));
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return result.toString();
    }

    /**
     * API验证核心 验证请求时效
     * @param response
     */
    private static boolean handlerAsd(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = getGzipWriter(request, response);
        setHandler(response);
        Map<String, Object> result = new HashMap<String, Object>(4);
        String tonkenAsd = request.getHeader("Tonken-ASD");
        if (null != tonkenAsd) {
            if (isNumeric(tonkenAsd)) {
                if (verifyTotpFlexibility("fuck",tonkenAsd)) {
                    return true;
                }
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                result.put("state", code("INVALID_VERIFICATION"));
                out.println(stringToUtf8(JSON.toJSONString(result)));
                out.close();
                return false;
            }
            // 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("state", code("ILLEGAL_OPERATION"));
            out.println(stringToUtf8(JSON.toJSONString(result)));
            out.close();
            return false;
        }
        // 400
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        result.put("state", code("INCOMPLETE_PARAMETERS"));
        out.println(stringToUtf8(JSON.toJSONString(result)));
        out.close();
        return false;
    }

    /**
     * Core
     */
    private class Info extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            if (!handlerAsd(request,response)) {
                return;
            }
            PrintWriter out = getGzipWriter(request, response);
            setHandler(response);
            String user = request.getParameter("user");
            String info = generateStr(10);
            PlayerData data = new PlayerData(info, info, 0);
            Data.SQL.getSqlite(data, user);
            Map<String, Object> result = new HashMap<String, Object>(4);
            result.put("state", code("SUCCESS"));
            result.put("result", new Base64().encode(JSON.toJSONString(data)));
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
                result.put("result", new Base64().encode(JSON.toJSONString(map)));
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
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = getGzipWriter(request, response);
            String data = getPostDate(request);
            Log.info(data);
            setHandler(response);
            Map<String, Object> result = new HashMap<String, Object>(4);
            if (notisBlank(data)) {
                if (isBlank(Data.PRIVATEKEY)) {
                    if (verifyTotpFlexibility("fuck",data)) {
                        try {
                            KeyPair keyPair = new Rsa().buildKeyPair();
                            Data.PRIVATEKEY = keyPair.getPrivate();
                            result.put("state", code("SUCCESS"));
                            result.put("pubkey", Rsa.getPublicKey(keyPair.getPublic()));
                        } catch (NoSuchAlgorithmException e) {
                            // 500
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            result.put("state", code("UNSUPPORTED_ENCRYPTION"));
                            Log.error(error("UNSUPPORTED_ENCRYPTION"),e);
                        }
                    } else {
                        // 401
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        result.put("state", code("NO_PERMISSION"));
                    }
                } else {
                    if (isBase64(data)) {
                        try {
                            byte[] secret = Rsa.decrypt(Data.PRIVATEKEY, new Base64().decode(data));
                            Integer.parseInt(new String(secret,Data.UTF_8));
                        } catch (BadPaddingException e) {
                            // 400
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            result.put("state", code("INVALID_ENCRYPTION"));
                        } catch (IllegalBlockSizeException e) {
                            // 500
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            result.put("state", code("DATA_CORRUPTION"));
                        }
                    } else {
                        // 415
                        response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                        result.put("state", code("NO_ENCRYPTION"));
                    }
                }
            } else {
                // 400
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
    /**
     * 自定义实现方法23333
     * 思路
     * 文件后辍为html js css jpg png返回数据 其他一律去世
     */
    private class Web extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        }
    }
}