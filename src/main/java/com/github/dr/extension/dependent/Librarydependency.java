package com.github.dr.extension.dependent;

import com.github.dr.extension.util.file.FileUtil;
import com.github.dr.extension.util.log.Log;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import static com.github.dr.extension.net.HttpRequest.url302;
import static com.github.dr.extension.net.HttpRequest.downUrl;

//Java
//GA-Exted
//Static

public class Librarydependency implements Driver {

    private static String url;
    private static Driver driver;

    Librarydependency(Driver d) {
        driver = d;
    }

    private static void downLoadFromUrl(String str, String name, String version, String country, String savePath) {
        String[] temp = str.split("\\.");
        StringBuilder uurl = new StringBuilder(64);
        uurl.append("/");
        for (int i = 0; i < temp.length; i++) {
            uurl.append(temp[i] + "/");
        }
        if ("China".equalsIgnoreCase(country)) {
            url = "https://maven.aliyun.com/nexus/content/groups/public" + uurl.toString() + name + "/" + version + "/" + name + "-" + version + ".jar";
            // 解决aliyun 302跳转
            url302(url, savePath);
            Log.debug("CN-ALI");
        } else {
            url = "https://repo1.maven.org/maven2" + uurl.toString() + name + "/" + version + "/" + name + "-" + version + ".jar";
            downUrl(url, savePath);
            Log.debug("NO CN-MAVEN");
        }
    }


    public static void importLib(String str, String name, String version, String savePath, String classname) {
        File filepath = new File(FileUtil.File(savePath).getPath());
        if (!filepath.exists()) {
            filepath.mkdirs();
        }
        List<File> filePathList = FileUtil.File(savePath).getFileList();

        for (int i = 0, len = filePathList.size(); i < len; i++) {
            if ((name + "_" + version).equals(filePathList.get(i).getName().replace(".jar", ""))) {
                notWork(name, version, savePath, classname);
                return;
            }
        }
        downLoadFromUrl(str, name, version, "China", FileUtil.File(savePath).getPath(name + "_" + version + ".jar"));
        notWork(name, version, savePath, classname);
    }

    private static void notWork(String name, String version, String savePath, String classname) {
        Log.info("START import SQL");
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(FileUtil.File(savePath).getPath(name + "_" + version + ".jar")).toURI().toURL()});
            Driver driver = (Driver) Class.forName(classname, true, classLoader).getDeclaredConstructor().newInstance();
            // 加壳
            DriverManager.registerDriver(new Librarydependency(driver));
            Log.info("SQL import Done");
        } catch (Exception e) {
            Log.error("import SQL", e);
        }
    }

    @Override
    public Connection connect(String u, Properties p) throws SQLException {
        return driver.connect(u, p);
    }


    @Override
    public boolean acceptsURL(String u) throws SQLException {
        return driver.acceptsURL(u);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
        return driver.getPropertyInfo(u, p);
    }

    @Override
    public int getMajorVersion() {
        return driver.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return driver.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return driver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return driver.getParentLogger();
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