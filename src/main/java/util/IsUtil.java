package extension.util;

import java.util.regex.Pattern;

public class IsUtil {
	
	public static boolean Blank(Object string) {
		if (string == null || "".equals(string.toString().trim())) {
            return true;
        }
		return false;
	}

	public static boolean NotBlank(Object string) {
		return !Blank(string);
	}

	public static boolean isNumeric(String string) {
		return Pattern.compile("[0-9]*").matcher(string).matches();
	}

	public static boolean NotisNumeric(String string) {
		return !isNumeric(string);
	}
}