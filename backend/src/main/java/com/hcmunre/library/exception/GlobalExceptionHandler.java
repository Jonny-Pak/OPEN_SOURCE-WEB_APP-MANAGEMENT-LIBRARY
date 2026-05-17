package com.hcmunre.library.exception;

import com.hcmunre.library.dto.response.ErrorResponse;
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
    @ExceptionHandler(LibraryException.class)
    public ResponseEntity<ErrorResponse> handleLibraryException(LibraryException ex, HttpServletRequest request){
        ErrorCode code = ex.getErrorCode();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(code.getHttpStatus().value())
                .error(code.name())
                .message(code.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(code.getHttpStatus()).body(errorResponse);
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
                .status(code.getHttpStatus().value())
                .error(code.name())
                .message(code.getMessage())
                .path(request.getRequestURI())
                .details(errors)
                .build();

        return ResponseEntity.status(code.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler({org.springframework.security.access.AccessDeniedException.class, org.springframework.security.core.AuthenticationException.class})
    public void handleSecurityException(Exception ex) throws Exception {
        throw ex; // Nhường lại cho CustomAccessDeniedHandler và CustomAuthenticationEntryPoint xử lý
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request){
        ex.printStackTrace();

        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();

        ErrorCode code = ErrorCode.SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(code.getHttpStatus().value())
                .error(code.name())
                .message(ex.getMessage() != null ? ex.getMessage() + " | " + stackTrace : code.getMessage() + " | " + stackTrace)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(code.getHttpStatus()).body(errorResponse);
    }
}
