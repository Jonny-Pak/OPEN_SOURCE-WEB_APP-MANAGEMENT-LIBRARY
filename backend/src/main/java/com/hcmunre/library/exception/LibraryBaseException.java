package com.hcmunre.library.exception;

import lombok.Getter;

@Getter
public class LibraryBaseException extends RuntimeException{
    private final ErrorCode errorCode;

    public LibraryBaseException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
