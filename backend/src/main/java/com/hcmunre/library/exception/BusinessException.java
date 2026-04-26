package com.hcmunre.library.exception;

public class BusinessException extends LibraryBaseException{
    public BusinessException(ErrorCode errorCode){
        super(errorCode);
    }
}
