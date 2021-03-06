package com.github.dr.extension.util.alone;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

//Java
//Javax

public class Password {
	/**
	 * 密码哈希：
	 * @param passwd
	 * @param salt
	 * @return，返回哈希
	 * ITERATIONS=128: 迭代次数RFC2898  HASH_BIT_SIZE=256: 摘要长度
	 */
	private static String genPasswordHash(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {	
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), 128, 256);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		byte[] hash = skf.generateSecret(spec).getEncoded();
		return Base64.getEncoder().encodeToString(hash);
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
		return Base64.getEncoder().encodeToString(salt);
	}
	

    public static boolean isPasswdVerify(String password, String passHash, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String hash = genPasswordHash(password, salt);
		return hash.equals(passHash);
	}


    public static Map<String, Object> newPasswd(String pw) throws NoSuchAlgorithmException, InvalidKeySpecException {
		Map<String, Object> password = new HashMap<String, Object>(4);
		String salt = genRandomSalt();
		// 经过加盐后的密码摘要
		String passwordHash = genPasswordHash(pw, salt);
		// 同时储存密码hash和盐
		password.put("passwordHash",passwordHash);
		password.put("salt",salt);
		boolean resualt = isPasswdVerify(pw, passwordHash, salt);
		// 验证密码
		password.put("resualt",resualt);
		
		return password;
	}

}