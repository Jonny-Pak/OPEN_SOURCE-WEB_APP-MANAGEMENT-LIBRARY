package com.hcmunre.library.exception;

/**
 * Error codes for API responses
 */
public enum ErrorCode {
    // User related errors
    USER_NOT_FOUND("ERR_USER_001", "Người dùng không tồn tại"),
    USER_ALREADY_EXISTS("ERR_USER_002", "Email hoặc số điện thoại đã được sử dụng"),
    INVALID_CREDENTIALS("ERR_USER_003", "Tên đăng nhập hoặc mật khẩu không đúng"),

    // Authentication errors
    UNAUTHORIZED("ERR_AUTH_001", "Chưa đăng nhập"),
    INVALID_TOKEN("ERR_AUTH_002", "Token không hợp lệ"),
    TOKEN_EXPIRED("ERR_AUTH_003", "Token đã hết hạn"),
    ACCESS_DENIED("ERR_AUTH_004", "Không có quyền truy cập"),

    // Validation errors
    INVALID_PASSWORD("ERR_VALIDATION_001", "Mật khẩu cũ không đúng"),
    PASSWORD_SAME("ERR_VALIDATION_002", "Mật khẩu mới phải khác với mật khẩu cũ"),
    INVALID_PHONE("ERR_VALIDATION_003", "Số điện thoại không hợp lệ"),
    INVALID_EMAIL("ERR_VALIDATION_004", "Email không hợp lệ"),
    INVALID_CCCD("ERR_VALIDATION_005", "CCCD không hợp lệ"),
    INVALID_AGE("ERR_VALIDATION_006", "Người dùng phải >= 15 tuổi"),

    // Resource not found errors
    RESOURCE_NOT_FOUND("ERR_RESOURCE_001", "Tài nguyên không tồn tại"),

    // Internal errors
    INTERNAL_SERVER_ERROR("ERR_SYSTEM_001", "Lỗi hệ thống");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
