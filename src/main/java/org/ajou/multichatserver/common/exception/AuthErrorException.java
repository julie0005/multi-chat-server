package org.ajou.multichatserver.common.exception;

import lombok.Getter;
import org.ajou.multichatserver.common.ErrorCode;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AuthErrorException extends AuthenticationException {

    private final ErrorCode errorCode;

    public AuthErrorException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}