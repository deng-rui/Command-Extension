package extension.util.log;

import extension.util.Log;

public class Exceptions {

	public static Exception Variable(String type) {
		return new VariableException(type);
	}

	public static Exception Net(String type) {
		return new NetException(type);
	}

	public static Exception File(String type) {
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
class NetException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NetException(String type) {
		super(ErrorCode.valueOf(type).getError());
	}

}

// 文件
class FileException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileException(String type) {
		super(ErrorCode.valueOf(type).getError());
	}

}