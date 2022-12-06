package org.ajou.multichatserver.common.exception;

import org.ajou.multichatserver.common.ErrorCode;

public class NoSuchElementException extends RuntimeException {

    private final ErrorCode errorCode;

    public NoSuchElementException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
