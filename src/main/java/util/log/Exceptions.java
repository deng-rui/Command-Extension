package extension.util.log;

import extension.util.log.Log;

public class Exceptions {

	public static VariableException Variable(String type) {
		return new VariableException(type);
	}

	public static NetException Net(String type) {
		return new NetException(type);
	}

	public static FileException File(String type) {
		return new FileException(type);
	}

}

// 变量
class VariableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public VariableException(String type) {
		super(ErrorCode.valueOf(type).getError());
	}

}

// 网络
class NetException extends Exception {

	private static final long serialVersionUID = 1L;

	public NetException(String type) {
		super(ErrorCode.valueOf(type).getError());
	}

}

// 文件
class FileException extends Exception {

	private static final long serialVersionUID = 1L;

	public FileException(String type) {
		super(ErrorCode.valueOf(type).getError());
	}

}