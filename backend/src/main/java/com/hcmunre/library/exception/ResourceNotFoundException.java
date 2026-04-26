package com.hcmunre.library.exception;

public class ResourceNotFoundException extends LibraryBaseException{
    public ResourceNotFoundException(ErrorCode errorCode){
        super(errorCode);
    }
}
