package extension.util.file;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
//Java

import extension.util.log.Log;
//GA-Exted

public class FileUtil {

	private static File file;
	private static String filepath;

	public FileUtil(String filepath){
		this.filepath = filepath;
		this.file = new File(filepath);
	}

	public FileUtil(File file, String filepath){
		this.file = file;
		this.filepath = filepath;
	}

	public synchronized static FileUtil File(String tofile) {
		File file;
		String filepath;
		String to = tofile;
		if (null!=tofile)
			if(!String.valueOf(tofile.charAt(0)).equals("/"))to = "/"+tofile;
		try {
			File directory = new File("");
			filepath=directory.getCanonicalPath()+to;
			file = new File(filepath);
			if (null==tofile)
				file = new File(directory.getCanonicalPath());
		} catch (Exception e) {	
			filepath=System.getProperty("user.dir")+to;
			file = new File(filepath);
			if (null==tofile)
				file = new File(System.getProperty("user.dir"));
		}
		return new FileUtil(file,filepath);
	}

	public static boolean exists() {
		return (file.exists());
	}

	public static String getPath() {
		return filepath;
	}

	public static String getPath(String filename) {
		String temp = filepath;
		new FileUtil(temp+"/"+filename);
		return temp+"/"+filename;
	} 

	public static FileUtil toPath(String filename) {
		return new FileUtil(filepath+"/"+filename);
	}

	public static List<File> getFileList() {
		File[] array = file.listFiles();
		List<File> filelist = new ArrayList<File>();
		for(int i=0;i<array.length;i++){
			if(!array[i].isDirectory())if(array[i].isFile())filelist.add(array[i]);
		}
		return filelist;
	}

	public synchronized static void writefile(Object log, boolean cover) {
		OutputStreamWriter osw = null;
		try {
			File parent = file.getParentFile();
			if(!parent.exists())parent.mkdirs();
			if(!file.exists())file.createNewFile();
			FileOutputStream write = new FileOutputStream(file,cover);
			osw = new OutputStreamWriter(write, "UTF-8"); 
			osw.write(log.toString());
			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null == osw) return;
			try {
				osw.flush();
			} catch (IOException e) {
				throw new RuntimeException("");
			}
		}
	}

	public static InputStreamReader readconfig() {
		try {
			return new InputStreamReader(new FileInputStream(file), "UTF-8");
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		return null;
	}

	public static Object readfile(boolean list) {
		try {
			return readfile(list,new InputStreamReader(new FileInputStream(file), "UTF-8"));
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		return null;
	}

	public static Object readfile(boolean list, InputStreamReader isr) {
		try { 
			BufferedReader br = new BufferedReader(isr); 
			String line = null; 
			if(list){
				List<String> FileContent = new ArrayList<String>();
				while ((line = br.readLine()) != null) { 
					FileContent.add(line);
				} 
				return FileContent;
			} else {
				String FileContent = "";
				while ((line = br.readLine()) != null) { 
					FileContent += line; 
					FileContent += "\r\n";
				}
				return FileContent;
			}
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		return null;
	}

}