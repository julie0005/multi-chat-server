package org.ajou.multichatserver.config.exception;

import lombok.Getter;
import org.ajou.multichatserver.config.ErrorCode;

@Getter
public class InvalidRequestException extends RuntimeException {

    private final ErrorCode errorCode;
    public InvalidRequestException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}