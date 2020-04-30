package extension.util.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha {


    public String sha256(final String strText) {
		return toSha(strText, "SHA-256");
	}


    public String sha512(final String strText) {
		return toSha(strText, "SHA-512");
	}


    private String toSha(final String strText, final String strType) {
		// 返回值
		String strResult = null;

		// 是否是有效字符串
		if (strText != null && strText.length() > 0) {
			try {
				MessageDigest messageDigest = MessageDigest.getInstance(strType);
				messageDigest.update(strText.getBytes());
				byte[] byteBuffer = messageDigest.digest();
				StringBuffer strHexString = new StringBuffer();
				for (int i = 0; i < byteBuffer.length; i++) {
					String hex = Integer.toHexString(0xff & byteBuffer[i]);
					if (hex.length() == 1) {
                        strHexString.append('0');
                    }
					strHexString.append(hex);
				}
				// 得到返回結果
				strResult = strHexString.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		return strResult;
	}
}