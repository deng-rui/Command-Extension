package extension.util.log;

public enum ErrorCode {
	//成功
	SUCCESS(0,"Success"),

	//未知
	ERROR(-1,"Error"),

	/*网络层*/
	NETWORK_NOT_CONNECTED(10001,"Network not connected"),
	
	/*客户端层*/
	PASSWORD_ERROR(20001,"Passwd error"),
	USER_DOES_NOT_EXIST(20002,"User does not exist"),
	SERVER_CLOSE(20003,"Server close"),

	
	/*文件层*/
	INVALID_PARAMETER(30001,"Please check the configuration file"),

	/*返回*/
	INCOMPLETE_PARAMETERS(40001,"Incomplete parameters");

	
	
	private int errCode;
	private String errMsg;

    private ErrorCode(int errorCode, String errorMsg) {
		this.errCode = errorCode;
		this.errMsg  = errorMsg;
	}

	public String getError() {
		return "["+this.errCode+"] : "+this.errMsg;
	}

	public int getCode() {
		return this.errCode;
	}

}