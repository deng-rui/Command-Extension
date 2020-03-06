package extension.util;

import java.util.*;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import java.io.StringWriter;
//Java

import static extension.util.DateUtil.getLocalTimeFromUTC;
//Static

public class LogUtil {

	private static final String TOP_BORDER     = "╔═══════════════════════════════════════════════════════════════════════════════════════════════════";
	private static final String LEFT_BORDER    = "║ ";
	private static final String BOTTOM_BORDER  = "╚═══════════════════════════════════════════════════════════════════════════════════════════════════";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static void fatal(Exception e) {
		log(7,"FATAL",e);
	}
	public static void fatal(String tag, Exception e) {
		log(7,tag,e);
	}

	public static void error(Exception e) {
		log(6,"ERROR",e);
	}
	public static void error(String tag, Exception e) {
		log(6,tag,e);
	}

	public static void warn(Exception e) {
		log(5,"WARN",e);
	}
	public static void warn(String tag, Exception e) {
		log(5,tag,e);
	}

	public static void info(Exception e) {
		log(4,"INFO",e);
	}
	public static void info(String tag, Exception e) {
		log(4,tag,e);
	}

	public static void debug(Exception e) {
		log(3,"DEBUG",e);
	}
	public static void debug(String tag, Exception e) {
		log(3,tag,e);
	}

	public static void tarce(Exception e) {
		log(2,"TARCE",e);
	}
	public static void tarce(String tag, Exception e) {
		log(2,tag,e);
	}

	public static void all(Exception e) {
		log(1,"ALL",e);
	}
	public static void all(String tag, Exception e) {
		log(1,tag,e);
	}

	private static void log(int i, String tag, Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append(TOP_BORDER)
			.append(LINE_SEPARATOR);
		sb.append(LEFT_BORDER)
			.append(getLocalTimeFromUTC(0,0))
			.append(LINE_SEPARATOR);
		sb.append(LEFT_BORDER)
			.append(tag)
			.append(": ")
			.append(LINE_SEPARATOR);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		StringBuffer error = stringWriter.getBuffer();
		String[] lines = error.toString().split(LINE_SEPARATOR);
			for (Object line : lines) {
				sb.append(LEFT_BORDER)
					.append(line)
					.append(LINE_SEPARATOR);
			}
		sb.append(BOTTOM_BORDER)
			.append(LINE_SEPARATOR);
		System.out.println(sb);
	}
}