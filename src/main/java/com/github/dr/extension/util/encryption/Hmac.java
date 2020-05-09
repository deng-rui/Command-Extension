package com.github.dr.extension.util.encryption;

import com.github.dr.extension.data.global.Data;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * @author Dr
 * @Data 2020/5/6 16:54
 */
public class Hmac {

    /**
     * 初始化HMAC密钥 (随机)
     * @param crypto   加密算法
     * @return
     * @throws Exception
     */
    public static String initMacKey(String crypto) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(crypto);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 摘要
     * @param key  用户密钥
     * @param text 摘要内容
     * @return
     */
    public byte[] hmacMD5(String key, String text) {
        return toHmac("HmacSHA1",key,text);
    }

    public byte[] hmacSha1(String key, String text) {
        return toHmac("HmacSHA1",key,text);
    }

    public byte[] hmacSha256(String key, String text) {
        return toHmac("HmacSHA256",key,text);
    }

    public byte[] hmacSha512(String key, String text) {
        return toHmac("HmacSHA512",key,text);
    }

    /**
     * HMAC加密 核心
     * @param crypto   加密算法
     * @param keyBytes 密钥数组
     * @param text     加密内容
     * @return
     */
    private byte[] encryptHmac(String crypto, byte[] keyBytes, byte[] text) {
        try {
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey = new SecretKeySpec(keyBytes, "AES");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    /**
     * HMAC摘要
     * @param crypto  加密算法
     * @param key     密钥
     * @param text    加密内容
     * @return
     */
    private byte[] toHmac(String crypto, String key, String text) {
        return encryptHmac(crypto,key.getBytes(Data.UTF_8), text.getBytes(Data.UTF_8));
    }
}