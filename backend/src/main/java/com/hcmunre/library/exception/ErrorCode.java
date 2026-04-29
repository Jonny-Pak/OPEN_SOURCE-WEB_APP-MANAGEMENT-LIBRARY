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
     * <h1>401 - Unauthorized (Chưa xác thực)</h1>
     */
    CHUA_DANG_NHAP(401, "Bạn chưa đăng nhập hoặc phiên đăng nhập đã hết hạn"),
    DANG_NHAP_THAT_BAI(401, "Tên đăng nhập hoặc mật khẩu không đúng"),

    /**
     * <h1>403 - Forbidden (Không có quyền truy cập)</h1>
     */
    KHONG_CO_QUYEN(403, "Bạn không có quyền truy cập tài nguyên này"),

    /**
     * <h1>409 - Conflict (Dữ liệu đã tồn tại)</h1>
     */
    EMAIL_DA_TON_TAI(409, "Email này đã được sử dụng"),

    /**
     * <h1>404 - Not Found (Người dùng)</h1>
     */
    NGUOI_DUNG_KHONG_TON_TAI(404, "Không tìm thấy người dùng"),

    /**
     * <h1>423 - Locked (Tài khoản bị khóa)</h1>
     */
    TAI_KHOAN_BI_KHOA(423, "Tài khoản của bạn đã bị khóa"),

    /**
     * <h1>500 - Internal Server</h1>
     */
    SERVER_ERROR(500, "Hệ thống đang gặp sự cố, vui lòng thử lại sau");
    private final int status;
    private final String message;
}
