package extension.tool;

import arc.Core;
import arc.files.Fi;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

import static extension.tool.HttpRequest.Url302;
import static extension.tool.HttpRequest.downUrl;

public class Librarydependency {

	private static String url;

	public static void downLoadFromUrl(String str, String name, String version, String country, Fi savePath) {
		String[] temp=str.split("\\.");
		url = "/";
		for (int i = 0; i < temp.length; i++) {
			System.out.println(temp[i]);
			url = url+temp[i]+"/";
		}

		if("China".equalsIgnoreCase(country)) {
			url = "https://maven.aliyun.com/nexus/content/groups/public"+url+name+"/"+version+"/"+name+"-"+version+".jar";
			Url302(url,name+"_"+version+".jar",savePath);
			//ali 302刺激 CN
		}else{
			url = "https://repo1.maven.org/maven2"+url+name+"/"+version+"/"+name+"-"+version+".jar";
			downUrl(url,name+"_"+version+".jar",savePath);
			//maven NO CN	
		}
	}

	public static void importLib(String str, String name, String version, Fi savePath) {
		String[] information;
		Fi[] file = savePath.list();
		List<String> list = new ArrayList<String>();
		for(int i=0;i<file.length;i++){
			list.add(file[i].name().replace(".jar",""));
		}
		if(!list.contains(name+"_"+version))downLoadFromUrl(str,name,version,"China",savePath);
	}
	

	public Connection loadLib_SQL(String str, String version, Fi savePath) {
		//? :(
	}
	/*动态加载指定类
        File file=new File("D:/test");//类路径(包文件上一层)
        URL url=file.toURI().toURL();
        ClassLoader loader=new URLClassLoader(new URL[]{url});//创建类加载器
        //import com.sun.org.apache.bcel.internal.util.ClassLoader;
        //ClassLoader classLoader = new ClassLoader(new String[]{""});//类路径
        Class<?> cls=loader.loadClass("loadjarclass.TestTest");//加载指定类，注意一定要带上类的包名
        Object obj=cls.newInstance();//初始化一个实例
        Method method=cls.getMethod("printString",String.class,String.class);//方法名和对应的参数类型
        Object o=method.invoke(obj,"chen","leixing");//调用得到的上边的方法method
        System.out.println(String.valueOf(o));//输出"chenleixing"
        
        /*动态加载指定jar包调用其中某个类的方法
        file=new File("D:/test/commons-lang3.jar");//jar包的路径
        url=file.toURI().toURL();
        loader=new URLClassLoader(new URL[]{url});//创建类加载器
        cls=loader.loadClass("org.apache.commons.lang3.StringUtils");//加载指定类，注意一定要带上类的包名
        method=cls.getMethod("center",String.class,int.class,String.class);//方法名和对应的各个参数的类型
        o=method.invoke(null,"chen",Integer.valueOf(10),"0");//调用得到的上边的方法method(静态方法，第一个参数可以为null)
        System.out.println(String.valueOf(o));//输出"000chen000","chen"字符串两边各加3个"0"字符串*/
	
}