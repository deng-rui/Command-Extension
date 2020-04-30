package extension.util.log;

public class NetException extends Exception {
    public NetException(String type) {
		super(extension.util.log.ErrorCode.valueOf(type).getError());
	}
}