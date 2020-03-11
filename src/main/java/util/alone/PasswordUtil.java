package extension.util.alone;

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
//Java

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
//Javax

import org.apache.commons.codec.binary.Base64;
//apach

public class PasswordUtil {
	/**
	 * 密码哈希：
	 * @param passwd
	 * @param salt
	 * @return，返回哈希
	 * ITERATIONS=128: 迭代次数RFC2898  HASH_BIT_SIZE=256: 摘要长度
	 */
	private static String genPasswordHash(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {	
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.decodeBase64(salt), 128, 256);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		byte[] hash = skf.generateSecret(spec).getEncoded();
		return Base64.encodeBase64String(hash);
	}

	/**
	 * 盐：
	 * @param 
	 * @return 返回盐
	 * SALT_BIT_SIZE=64: 盐长度RFC2898
	 */
	private static String genRandomSalt() {
		byte[] salt = new byte[64];
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(salt);
		return Base64.encodeBase64String(salt);
	}
	
	public static boolean Passwdverify(String password, String passHash, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String hash = genPasswordHash(password, salt);
		return hash.equals(passHash);
	}

	public static Map<String, Object> newPasswd(String pw) throws NoSuchAlgorithmException, InvalidKeySpecException {
		Map<String, Object> Password = new HashMap<String, Object>();
		String salt = genRandomSalt();
		// 经过加盐后的密码摘要
		String passwordHash = genPasswordHash(pw, salt);
		// 同时储存密码hash和盐
		Password.put("passwordHash",passwordHash);
		Password.put("salt",salt);
		boolean resualt = Passwdverify(pw, passwordHash, salt);
		// 验证密码
		Password.put("resualt",resualt);
		
		return Password;
	}

}