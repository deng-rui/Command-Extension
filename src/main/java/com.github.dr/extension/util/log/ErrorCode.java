package com.github.dr.extension.util.log;

/**
 * @author Dr
 */
public enum ErrorCode {
	//成功
	SUCCESS(0,"Success"),

	//未知
	ERROR(-1,"Error"),

	/*网络层*/
	NETWORK_NOT_CONNECTED(10001,"Network not connected"),
	DATA_CORRUPTION(10002,"Data corruption"),
	
	/*客户端层*/
	PASSWORD_ERROR(20001,"Passwd error"),
	USER_DOES_NOT_EXIST(20002,"User does not exist"),
	SERVER_CLOSE(20003,"Server close"),
	
	/*文件层*/
	INVALID_PARAMETER(30001,"Please check the configuration file"),

	/*WEB层*/
	INCOMPLETE_PARAMETERS(40001,"Incomplete parameters"),

	/*加密层*/
	NO_ENCRYPTION(50001,"No Encryption"),
	INVALID_ENCRYPTION(50002,"Invalid encryption"),
	ILLEGAL_OPERATION(50003,"Illegal operation"),
	UNSUPPORTED_ENCRYPTION(50004,"Unsupported encryption"),
	DOES_NOT_SUPPORT_AES_256(50005,"Does not support ASE256");
	
	
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