package extension.tool;

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Password {
	//我觉得真就多此一举

	private static final int HASH_BIT_SIZE = 256;
	//摘要长度
	private static final int ITERATIONS = 128;
	//迭代次数RFC2898
	private static final int SALT_BIT_SIZE = 64;
	//盐长度RFC2898

	public static String genPasswordHash(String password, String salt) throws Exception {
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.decodeBase64(salt), ITERATIONS, HASH_BIT_SIZE);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		byte[] hash = skf.generateSecret(spec).getEncoded();
		return Base64.encodeBase64String(hash);
	}
	//密码摘要
	
	public static String genRandomSalt() {
		byte[] salt = new byte[SALT_BIT_SIZE];
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(salt);
		return Base64.encodeBase64String(salt);
	}
	//生成随机盐
	
	public static boolean verify(String password, String passHash, String salt) throws Exception {
		String hash = genPasswordHash(password, salt);
		return hash.equals(passHash);
	}
	//验证密码

	public static void savePasswordDemo(String passwordHash, String salt) {
		System.out.println("passwordHash : "+passwordHash);
		System.out.println("salt : "+salt);
	}
	//密码Hash 和 salt 同时存储

	public static void aaa(String a) {
		//原始密码
		//生成随机盐
		try {
		String salt = genRandomSalt();
		//经过加盐后的密码摘要
		String passwordHash = genPasswordHash(a, salt);
		//同时储存密码hash和盐
		savePasswordDemo(passwordHash, salt);
		//验证密码
		boolean resualt = verify(a, passwordHash, salt);
		System.out.println(resualt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void aab(String a,String b,String c) {
		//原始密码
		//生成随机盐
		try {
		String salt = genRandomSalt();
		//经过加盐后的密码摘要
		String passwordHash = genPasswordHash(a, salt);
		//同时储存密码hash和盐
		savePasswordDemo(b, c);
		//验证密码
		boolean resualt = verify(a, b, c);
		System.out.println(resualt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}