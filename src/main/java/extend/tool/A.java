/*
//Debugging part
package extension.extend.tool;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
 
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.message.BasicHeader;
import java.util.Date;


import  org.apache.http.Consts;
import  org.apache.http.NameValuePair;
import  org.apache.http.client.CookieStore;
import  org.apache.http.client.config.CookieSpecs;
import  org.apache.http.client.config.RequestConfig;
import  org.apache.http.client.entity.UrlEncodedFormEntity;
import  org.apache.http.client.methods.CloseableHttpResponse;
import  org.apache.http.client.methods.HttpGet;
import  org.apache.http.client.methods.HttpPost;
import  org.apache.http.client.protocol.HttpClientContext;
import  org.apache.http.cookie.Cookie;
import  org.apache.http.impl.client.BasicCookieStore;
import  org.apache.http.impl.client.CloseableHttpClient;
import  org.apache.http.impl.client.HttpClients;
import  org.apache.http.message.BasicNameValuePair;
import  org.apache.http.util.EntityUtils;

public class A {


       public void gtCookie(String url){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get=new HttpGet(url);
        HttpClientContext context = HttpClientContext.create();
        try {
            CloseableHttpResponse response = httpClient.execute(get, context);
            try{
                System.out.println(">>>>>>headers:");
                Arrays.stream(response.getAllHeaders()).forEach(System.out::println);
                System.out.println(">>>>>>cookies:");
                List<org.apache.http.cookie.Cookie> cookie = context.getCookieStore().getCookies();
                for (int i = 0; i < cookie.size(); i++) {
                System.out.println(cookie.get(i));
            }
                //.forEach(System.out::println);
            }finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
if(cook.getName().equalsIgnoreCase("BAIDUID")){
                System.out.println("account:"+cook.getValue().toString());
                }
    public static void addjson(String xs) {
        JSONObject add = new JSONObject();
        add.put("languageO", xs);
        String json = add.toString();
        Core.settings.getDataDirectory().child("mods/GA/X.json").writeString(json);
    }
}
<script>window.bdstoken = '';320305.131321201;</script>
String regex;
    String title = "";
    List<String> list = new ArrayList<String>();
    regex = ;
    Pattern pa = Pattern.compile(regex, Pattern.CANON_EQ);
    Matcher ma = pa.matcher(s);
    while (ma.find()) {
      list.add(ma.group());
    }
    for (int i = 0; i < list.size(); i++) {
      title = title + list.get(i);
    }
    //return outTag();
    
  }

public class A {
      public void getTitle(String soapP) throws Exception {
        String soap;
        soap = removeAllBlank(soapP);
        //System.out.println(">>>>>>ID:"+soap);gtk: 320305.131321201'
        try {
            addjson(soap);
                    String matchString = findMatchString(soap, "gtk:'.*?'"); 
            //String matchString = findMatchString(soap, "tkk:.*?',");
                    String tkk = matchString.substring(5, matchString.length() - 2);
                    System.out.println(tkk);
        } catch (Exception e) {
        }
    }
//Json

    //
}

 
    public void getCookie(String url) {
     // 全局请求设置
     RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
     // 创建cookie store的本地实例
     CookieStore cookieStore =  new  BasicCookieStore();
     // 创建HttpClient上下文
     HttpClientContext context = HttpClientContext.create();
     context.setCookieStore(cookieStore);
 
     // 创建一个HttpClient
     CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig)
         .setDefaultCookieStore(cookieStore).build();
 
     CloseableHttpResponse res =  null ;
 
     // 创建本地的HTTP内容
     try  {
       try  {
         // 创建一个get请求用来获取必要的Cookie，如_xsrf信息
         HttpGet get =  new  HttpGet( url );
 
         res = httpClient.execute(get, context);
         // 获取常用Cookie,包括_xsrf信息
         for  (Cookie c : cookieStore.getCookies()) {
           //System.out.println(c.getName() +  ": "  + c.getValue());
         }
         res.close();
 
        
       }  finally  {
         httpClient.close();
       }

     }  catch  (IOException e) {
       e.printStackTrace();
     }
   }
*/
}

