package com.github.dr.extension.util.log;

public class VariableException extends RuntimeException {
    public VariableException(String type) {
        super(com.github.dr.extension.util.log.ErrorCode.valueOf(type).getError());
    }
}