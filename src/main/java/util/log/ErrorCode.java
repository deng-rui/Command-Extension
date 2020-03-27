package extension.util.log;

import extension.util.log.Error;

public enum ErrorCode {
	//成功
	SUCCESS(0,"Success"),

	//未知
	ERROR(-1,"Error"),

	/*网络层*/
	//无网络
	NETWORK_NOT_CONNECTED(10001,"Network not connected");
	
	/*客户端层*/
	
	
	/*文件层*/

	
	
	private int errCode;
	private String errMsg;

	private ErrorCode(int ErrorCode, String ErrorMsg) {
		this.errCode = ErrorCode;
		this.errMsg  = ErrorMsg;
	}

	public String getError() {
		return "["+this.errCode+"] : "+this.errMsg;
	}	

}