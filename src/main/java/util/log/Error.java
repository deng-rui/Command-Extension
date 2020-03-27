package extension.util.log;

import extension.util.log.ErrorCode;

public class Error {

	public static String Error(String type) {
		return ErrorCode.valueOf(type).getError();
	}
}