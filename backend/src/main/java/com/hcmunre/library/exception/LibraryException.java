package com.hcmunre.library.exception;

import lombok.Getter;

@Getter
public class LibraryException extends RuntimeException{
    private final ErrorCode errorCode;

    public LibraryException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
