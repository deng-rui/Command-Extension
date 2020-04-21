package extension.dependent;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.List;
import java.util.Properties;
//Java

import extension.util.file.FileUtil;
import extension.util.log.Log;
//GA-Exted

import static extension.net.HttpRequest.Url302;
import static extension.net.HttpRequest.downUrl;
//Static

public class Librarydependency implements Driver {

	private static String url;
	private static Driver driver;

	Librarydependency(Driver d) {
		this.driver = d;
	}

	private static void downLoadFromUrl(String str, String name, String version, String country, String savePath) {
		String[] temp=str.split("\\.");
		url = "/";
		for (int i = 0; i < temp.length; i++) {
			url = url+temp[i]+"/";
		}
		if("China".equalsIgnoreCase(country)) {
			url = "https://maven.aliyun.com/nexus/content/groups/public"+url+name+"/"+version+"/"+name+"-"+version+".jar";
			// 解决aliyun 302跳转
			Url302(url,savePath);
			Log.debug("CN-ALI");
		}else{
			url = "https://repo1.maven.org/maven2"+url+name+"/"+version+"/"+name+"-"+version+".jar";
			downUrl(url,savePath);
			Log.debug("NO CN-MAVEN");
		}
	}

	public static void importLib(String str, String name, String version, String savePath) {
		File filepath=new File(FileUtil.File(savePath).getPath());
		if (!filepath.exists())filepath.mkdirs();
		List<File> FilePathList = FileUtil.File(savePath).getFileList();
		
		for(int i=0;i<FilePathList.size();i++){
			if((name+"_"+version).equals(FilePathList.get(i).getName().replace(".jar",""))) {
				notWork(name,version,savePath);
				return;
			}
		}
		downLoadFromUrl(str,name,version,"China",FileUtil.File(savePath).getPath(name+"_"+version+".jar"));
		notWork(name,version,savePath);
	}

	private static void notWork(String name, String version, String savePath) {
		Log.info("START import SQL");
		try {
			URLClassLoader classLoader = new URLClassLoader(new URL[] {new File(FileUtil.File(savePath).getPath(name+"_"+version+".jar")).toURI().toURL()});
			Driver driver = (Driver) Class.forName("org.sqlite.JDBC", true, classLoader).getDeclaredConstructor().newInstance();
			// 加壳
			DriverManager.registerDriver(new Librarydependency(driver));
			Log.info("SQL import Done");
		} catch (Exception e){
			Log.error("import SQL",e);
		}
	}

	public Connection connect(String u, Properties p) throws SQLException {
		return this.driver.connect(u, p);
	}

	public boolean acceptsURL(String u) throws SQLException {
		return this.driver.acceptsURL(u);
	}

	public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
		return this.driver.getPropertyInfo(u, p);
	}

	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}

	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}

	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.driver.getParentLogger();
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