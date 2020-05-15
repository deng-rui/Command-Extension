package com.github.dr.extension.util.log.exp;

public class NetException extends Exception {
    public NetException(String type) {
		super(com.github.dr.extension.util.log.ErrorCode.valueOf(type).getError());
	}
}