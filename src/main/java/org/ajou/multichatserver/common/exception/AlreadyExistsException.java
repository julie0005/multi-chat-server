package org.ajou.multichatserver.common.exception;

import lombok.Getter;
import org.ajou.multichatserver.common.ErrorCode;

@Getter
public class AlreadyExistsException extends RuntimeException{

    private final ErrorCode errorCode;

    public AlreadyExistsException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}