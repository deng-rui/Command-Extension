package extension.util;

import java.util.regex.Pattern;

public class IsUtil {

	private final static Pattern pattern = Pattern.compile("[0-9]*");
	

    public static boolean isblank(Object string) {
		if (string == null || "".equals(string.toString().trim())) {
            return true;
        }
		return false;
	}


    public static boolean notBlank(Object string) {
		return !isblank(string);
	}

	public static boolean isNumeric(String string) {
		return pattern.matcher(string).matches();
	}


    public static boolean notisNumeric(String string) {
		return !isNumeric(string);
	}
}