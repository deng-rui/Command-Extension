package com.github.dr.extension.util.encryption;

import com.github.dr.extension.data.global.Data;
import com.github.dr.extension.util.log.Log;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.github.dr.extension.util.log.Error.error;

public class Rsa {
    /*
        public static void main(String [] args) throws Exception {
            // 获取钥匙
            KeyPair keyPair = buildKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String publicKeyString = base64Encode(publicKey.getEncoded());
            System.out.println("publicKeyString="+publicKeyString +"\n");
            // 加密
            byte [] encrypted = encrypt(publicKey, "This is a secret message");
            System.out.println(base64Encode(encrypted));  // <<encrypted message>>

            // 解密
            byte[] secret = decrypt(privateKey, encrypted);
            System.out.println(new String(secret, UTF8));     // This is a secret message
        }
    */
    public KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);
        return keyPairGenerator.genKeyPair();
    }

    public static byte[] encrypt(PublicKey publicKey, String message) throws BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
        /**
         * 这一部分的cache是没用的
         * NoSuchPaddingException : 无法获取实例
         * NoSuchAlgorithmException : 不支持的加密方法
         * InvalidKeyException : 大于ASE128
         */
        } catch (NoSuchPaddingException e) {
        } catch (NoSuchAlgorithmException e) {
            Log.error(error("UNSUPPORTED_ENCRYPTION"),e);
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        } catch (InvalidKeyException e) {
            Log.error(error("DOES_NOT_SUPPORT_AES_256"),e);
        }
        return cipher.doFinal(message.getBytes(Data.UTF_8));
    }

    public static byte[] decrypt(PrivateKey privateKey, byte[] encrypted) throws BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
        /**
         * 这一部分的cache是没用的
         * NoSuchPaddingException : 无法获取实例
         * NoSuchAlgorithmException : 不支持的加密方法
         * InvalidKeyException : 大于ASE128
         */
        } catch (NoSuchPaddingException e) {
        } catch (NoSuchAlgorithmException e) {
            Log.error(error("UNSUPPORTED_ENCRYPTION"),e);
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
        } catch (InvalidKeyException e) {
            Log.error(error("DOES_NOT_SUPPORT_AES_256"),e);
        }
        return cipher.doFinal(encrypted);
    }

    /**
     * 从字符串中加载公/私钥
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr) {
        try {
            byte[] buffer = Base64.getDecoder().decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAPrivateKey loadPrivateKey(String privateKeyStr) {
        try {
            byte[] buffer = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPublicKey(PublicKey publicKey) throws IOException {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static String getPrivateKey(PrivateKey privateKey) throws IOException {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
}