package com.github.dr.extension.util.log;

public class FileException extends Exception {
    public FileException(String type) {
        super(com.github.dr.extension.util.log.ErrorCode.valueOf(type).getError());
    }
}