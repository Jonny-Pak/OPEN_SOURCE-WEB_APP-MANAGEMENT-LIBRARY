package com.hcmunre.library.exception;

/**
 * Exception thrown when validation fails
 */
public class ValidationException extends AppException {

    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ValidationException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
