package extension.util.encryption;

import extension.data.global.Data;

import javax.crypto.Cipher;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Rsa {
    private static final String RSA_ALGORITHM = "RSA";
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
    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(4096);
        return keyPairGenerator.genKeyPair();
    }

    public static byte[] encrypt(PublicKey publicKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
        return cipher.doFinal(message.getBytes(Data.UTF8));
    }
    
    public static byte[] decrypt(PrivateKey privateKey, byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encrypted);
    }

    /**
     * 从字符串中加载公钥
     *
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = base64Decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = base64Decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPublicKey(PublicKey publicKey) throws IOException {
        return base64Encode(publicKey.getEncoded());
    }

    public static String getPrivateKey(PrivateKey privateKey) throws IOException {
        return base64Encode(privateKey.getEncoded());
    }

    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);

    }
    public static byte[] base64Decode(String data) throws IOException {
        return Base64.getDecoder().decode(data);
    }
}