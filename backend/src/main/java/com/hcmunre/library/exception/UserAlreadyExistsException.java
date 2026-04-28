package com.hcmunre.library.exception;

/**
 * Exception thrown when a user already exists
 */
public class UserAlreadyExistsException extends AppException {

    public UserAlreadyExistsException() {
        super(ErrorCode.USER_ALREADY_EXISTS);
    }

    public UserAlreadyExistsException(String customMessage) {
        super(ErrorCode.USER_ALREADY_EXISTS, customMessage);
    }
}
