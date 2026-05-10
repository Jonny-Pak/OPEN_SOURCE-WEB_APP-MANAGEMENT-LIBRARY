package com.hcmunre.library.exception;

/**
 * Exception cho lỗi 403 - Không có quyền truy cập (Forbidden).
 * Được ném ra khi người dùng đã xác thực nhưng không đủ quyền
 * để truy cập tài nguyên được yêu cầu.
 */
public class ForbiddenException extends LibraryException {
    public ForbiddenException() {
        super(ErrorCode.KHONG_CO_QUYEN);
    }
}
