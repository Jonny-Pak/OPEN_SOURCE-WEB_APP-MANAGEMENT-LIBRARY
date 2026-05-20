package com.hcmunre.library.exception;

import com.hcmunre.library.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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
        // Log full stack trace server-side only — never expose to API consumers
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorCode code = ErrorCode.SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(code.getHttpStatus().value())
                .error(code.name())
                .message(ex.getMessage() != null ? ex.getMessage() : code.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(code.getHttpStatus()).body(errorResponse);
    }
}
