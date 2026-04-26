package com.hcmunre.library.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /**
     * <h1>404 - Not Found
     * <h1>
     */
    SACH_KHONG_TON_TAI(404, "Không tìm thấy thông tin đầu sách này"),

    /**
     * <h1>500 - Bad Request
     * <h1>
     */
    INVALID_INPUT(400, "Dữ liệu đầu vào không hợp lệ"),

    /**
     * <h1>500 - Internal Server
     * <h1>
     */
    SERVER_ERROR(500, "Hệ thống đang gặp sự cố, vui lòng thử lại sau");
    private final int status;
    private final String message;
}
