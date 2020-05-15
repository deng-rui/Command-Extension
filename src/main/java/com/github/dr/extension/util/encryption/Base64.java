package com.github.dr.extension.util.encryption;

import com.github.dr.extension.data.global.Data;

/**
 * 已不适用 JAVA8
 */
public class Base64 {

	private final static char[] STR = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/'};

	/**
	 *
	 * @param str Base64字符串
	 * @return 解密后
	 */
	public byte[] decode(String str){
		return java.util.Base64.getDecoder().decode(str);
	}

	public String decodeString(String str){
		return new String(java.util.Base64.getDecoder().decode(str), Data.UTF_8);
	}
	/**
	 *
	 * @param str 文字
	 * @return 加密后
	 */
	public String encode(String str){
		return java.util.Base64.getEncoder().encodeToString(str.getBytes(Data.UTF_8));
	}

	public static boolean isBase64(String val) {
		try {
			byte[] key= java.util.Base64.getDecoder().decode(val);
			String strs=new String(key);
			String result= java.util.Base64.getEncoder().encodeToString(strs.getBytes());
			if(result.equals(val)) {
                return true;
            }
		} catch(Exception e){
		}
		return false;
	}
}