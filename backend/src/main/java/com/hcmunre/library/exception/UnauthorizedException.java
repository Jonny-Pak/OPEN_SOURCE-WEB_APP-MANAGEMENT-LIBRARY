package com.hcmunre.library.exception;

/**
 * Exception cho lỗi 401 - Người dùng chưa xác thực (Unauthorized).
 * Được ném ra khi người dùng truy cập tài nguyên mà chưa đăng nhập
 * hoặc token JWT không hợp lệ / đã hết hạn.
 */
public class UnauthorizedException extends LibraryException {
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
