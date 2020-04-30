package extension.util.log;

public class Error {

    public static String error(String type) {
		return extension.util.log.ErrorCode.valueOf(type).getError();
	}

    public static int code(String type) {
		return extension.util.log.ErrorCode.valueOf(type).getCode();
	}
}