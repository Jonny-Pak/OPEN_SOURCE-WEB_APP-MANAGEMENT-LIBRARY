package com.hcmunre.library.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LibraryBaseException.class)
    public ResponseEntity<ErrorResponse> handleLibraryException(LibraryBaseException ex, HttpServletRequest request){
        ErrorCode code = ex.getErrorCode();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(code.getStatus())
                .error(code.name())
                .message(code.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(code.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request){
        ErrorCode code = ErrorCode.INVALID_INPUT;

        Map<String, String > errors = new HashMap<>();
        for(FieldError error: ex.getBindingResult().getFieldErrors()){
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(code.getStatus())
                .error(code.name())
                .message(code.getMessage())
                .path(request.getRequestURI())
                .details(errors)
                .build();

        return ResponseEntity.status(code.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request){
        ex.printStackTrace();

        ErrorCode code = ErrorCode.SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(code.getStatus())
                .error(code.name())
                .message(code.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(code.getStatus()).body(errorResponse);
    }
}
