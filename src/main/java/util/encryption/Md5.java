package extension.util.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//Java

public class Md5 {

	private static final char[] HEXDIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd','e', 'f' };

	public static String md5(String input) {
		if (input == null) {
            return null;
        }
		try {
			byte[] resultByteArray = MessageDigest.getInstance("MD5").digest(input.getBytes("UTF-8"));
			return byteArrayToHex(resultByteArray);
		} catch (NoSuchAlgorithmException e) {
			//Log.error
		} catch (UnsupportedEncodingException e) {

		}
		return null;
	}

	public static String md5(File file) {
		try {
			if (!file.isFile()) {
                return null;
            }
			FileInputStream in = new FileInputStream(file);
			String result = md5(in);
			in.close();
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String md5(InputStream in) {
		try {
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int read = 0;
			while ((read = in.read(buffer)) != -1) {
                messagedigest.update(buffer, 0, read);
            }
			in.close();
			return byteArrayToHex(messagedigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String byteArrayToHex(byte[] byteArray) {
		char[] resultCharArray = new char[byteArray.length * 2];
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = HEXDIGITS[b >>> 4 & 0xf];
			resultCharArray[index++] = HEXDIGITS[b & 0xf];
		}
		return new String(resultCharArray);

	}

}
