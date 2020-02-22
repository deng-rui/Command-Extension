package extension.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;
//Java

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
//Javax

import org.apache.commons.codec.binary.Base64;
//apach

public class PasswordUtil {
	//我觉得真就多此一举

	private static final int HASH_BIT_SIZE = 256;
	//摘要长度
	private static final int ITERATIONS = 128;
	//迭代次数RFC2898
	private static final int SALT_BIT_SIZE = 64;
	//盐长度RFC2898

	public static String genPasswordHash(String password, String salt) {
		try {		
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.decodeBase64(salt), ITERATIONS, HASH_BIT_SIZE);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			byte[] hash = skf.generateSecret(spec).getEncoded();
			return Base64.encodeBase64String(hash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return genPasswordHash(password,salt);
	}
	//密码摘要
	
	public static String genRandomSalt() {
		byte[] salt = new byte[SALT_BIT_SIZE];
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(salt);
		return Base64.encodeBase64String(salt);
	}
	//生成随机盐
	
	public static boolean Passwdverify(String password, String passHash, String salt) {
		String hash = genPasswordHash(password, salt);
		return hash.equals(passHash);
	}
	//验证密码

	public static Map<String, Object> newPasswd(String pw) {
		Map<String, Object> Password = new HashMap<String, Object>();
		String salt = genRandomSalt();
		String passwordHash = genPasswordHash(pw, salt);
		//经过加盐后的密码摘要
		Password.put("passwordHash",passwordHash);
		Password.put("salt",salt);
		//同时储存密码hash和盐
		boolean resualt = Passwdverify(pw, passwordHash, salt);
		Password.put("resualt",resualt);
		//验证密码
		return Password;
	}

}