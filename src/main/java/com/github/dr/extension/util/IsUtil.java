package com.github.dr.extension.util;

import java.util.regex.Pattern;

public class IsUtil {

	private final static Pattern PATTERN = Pattern.compile("[0-9]*");
	

    public static boolean isBlank(Object string) {
		if (string == null || "".equals(string.toString().trim())) {
            return true;
        }
		return false;
	}


    public static boolean notisBlank(Object string) {
		return !isBlank(string);
	}

	public static boolean isNumeric(String string) {
		return PATTERN.matcher(string).matches();
	}


    public static boolean notisNumeric(String string) {
		return !isNumeric(string);
	}
}