package com.github.dr.extension.util.log.exp;

public class VariableException extends RuntimeException {
    public VariableException(String type) {
        super(com.github.dr.extension.util.log.ErrorCode.valueOf(type).getError());
    }
}