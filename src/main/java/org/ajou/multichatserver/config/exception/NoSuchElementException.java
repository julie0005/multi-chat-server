package org.ajou.multichatserver.config.exception;

import org.ajou.multichatserver.config.ErrorCode;

public class NoSuchElementException extends RuntimeException {

    private final ErrorCode errorCode;

    public NoSuchElementException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
