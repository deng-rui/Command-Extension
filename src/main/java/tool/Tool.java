package extension.tool;

import java.io.*;
import java.net.*;
import java.util.*;

public class Tool {
	public static boolean isBlank(String string) {
		if (string == null || "".equals(string.trim())) {
			return true;
		}
		return false;
	}

	public static boolean isNotBlank(String string) {
		return !isBlank(string);
	}
}