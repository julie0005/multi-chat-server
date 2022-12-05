package org.ajou.multichatserver.config.exception;

import lombok.Getter;
import org.ajou.multichatserver.config.ErrorCode;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AuthErrorException extends AuthenticationException {

    private final ErrorCode errorCode;

    public AuthErrorException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}