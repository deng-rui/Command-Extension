package extension.util;

public class RegularUtil {
	
	public static boolean Blank(String string) {
		if (string == null || "".equals(string.trim())) {
			return true;
		}
		return false;
	}

	public static boolean NotBlank(String string) {
		return !Blank(string);
	}
}