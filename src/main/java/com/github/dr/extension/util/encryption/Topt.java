package com.github.dr.extension.util.encryption;

import com.github.dr.extension.data.global.Config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;

import static com.github.dr.extension.util.DateUtil.getLocalTimeFromU;


/**
 * @author [Internet]
 * @Data 2020/5/8 9:26
 */
public class Topt {
    /**
     * 依赖 Hmac.java
     */

    /**
     * 时间步长 单位:秒 作为口令变化的时间周期
     * 10S存活
     * 来保证OPT即时性 若User -> Server-Web后时间过期 则可发二次码
     */
    private static final long STEP = 10;

    /**
     * 转码位数 [1-8]
     * 12345678
     */
    private static final int CODE_DIGITS = 8;

    /**
     * 初始化时间
     */
    private static final long INITIAL_TIME = 0;

    /**
     * 时间回溯
     * 5S User -> Ngrok/Frp -> Server-Web
     * or
     * 5S User -> Server-Web
     * 防止在前一个到期时发送旧码 即 发送后五秒内依然可验证
     */
    private static final long FLEXIBILIT_TIME = 5;

    /**
     * 数子量级
     */
    private static final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

    /**
     * 生成一次性密码
     * @param pass 密码
     * @return String
     */
    public static String newTotp(String pass) {
        long now = getLocalTimeFromU();
        String time = Long.toHexString(timeFactor(now)).toUpperCase();
        return generateTotp(pass + Config.TOPT_KEY, time);
    }

    /**
     * 刚性口令验证
     * @param pass 密码
     * @param totp 待验证的口令
     * @return boolean
     */
    public static boolean verifyTotpRigidity(String pass, String totp) {
        long now = getLocalTimeFromU();
        String time = Long.toHexString(timeFactor(now)).toUpperCase();
        return generateTotp(pass + Config.TOPT_KEY, time).equals(totp);
    }

    /**
     * 柔性口令验证
     * @param pass 密码
     * @param totp 待验证的口令
     * @return boolean
     */
    public static boolean verifyTotpFlexibility(String pass, String totp) {
        long now = getLocalTimeFromU();
        String time = Long.toHexString(timeFactor(now)).toUpperCase();
        String tempTotp = generateTotp(pass + Config.TOPT_KEY, time);
        if (tempTotp.equals(totp)) {
            return true;
        }
        String time2 = Long.toHexString(timeFactor(now - FLEXIBILIT_TIME)).toUpperCase();
        String tempTotp2 = generateTotp(pass + Config.TOPT_KEY, time2);
        return tempTotp2.equals(totp);
    }

    /**
     * 获取动态因子
     * @param targetTime 指定时间
     * @return long
     */
    private static long timeFactor(long targetTime) {
        return (targetTime - INITIAL_TIME) / STEP;
    }

    /**
     * 哈希加密
     * @param crypto   加密算法
     * @param keyBytes 密钥数组
     * @param text     加密内容
     * @return byte[]
     */
    private static byte[] hmacSha(String crypto, byte[] keyBytes, byte[] text) {
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

    private static byte[] hexStr2Bytes(String hex) {
        byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();
        byte[] ret = new byte[bArray.length - 1];
        System.arraycopy(bArray, 1, ret, 0, ret.length);
        return ret;
    }

    private static String generateTotp(String key, String time) {
        return generateTotp(key, time, "HmacSHA1");
    }


    private static String generateTotp256(String key, String time) {
        return generateTotp(key, time, "HmacSHA256");
    }

    private static String generateTotp512(String key, String time) {
        return generateTotp(key, time, "HmacSHA512");
    }

    private static String generateTotp(String key, String time, String crypto) {
        StringBuilder timeBuilder = new StringBuilder(time);
        int len = 16;
        while (timeBuilder.length() < len) {
            timeBuilder.insert(0, "0");
        }
        time = timeBuilder.toString();
        byte[] msg = hexStr2Bytes(time);
        byte[] k = key.getBytes();
        byte[] hash = hmacSha(crypto, k, msg);
        return truncate(hash);
    }

    /**
     * 截断函数
     * @param target 20字节的字符串
     * @return String
     */
    private static String truncate(byte[] target) {
        StringBuilder result;
        int offset = target[target.length - 1] & 0xf;
        int binary = ((target[offset] & 0x7f) << 24)
                | ((target[offset + 1] & 0xff) << 16)
                | ((target[offset + 2] & 0xff) << 8) | (target[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[CODE_DIGITS];
        result = new StringBuilder(Integer.toString(otp));
        while (result.length() < CODE_DIGITS) {
            result.insert(0, "0");
        }
        return result.toString();
    }

}
