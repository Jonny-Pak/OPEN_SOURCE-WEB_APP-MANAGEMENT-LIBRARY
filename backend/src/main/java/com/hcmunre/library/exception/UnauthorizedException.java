package com.hcmunre.library.exception;

/**
 * Exception thrown when user is not authorized
 */
public class UnauthorizedException extends AppException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
