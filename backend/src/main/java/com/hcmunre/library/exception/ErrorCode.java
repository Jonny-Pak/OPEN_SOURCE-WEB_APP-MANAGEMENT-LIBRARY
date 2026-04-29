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
    PHIEU_MUON_KHONG_TON_TAI(404, "Không tìm thấy phiếu mượn này"),
    CHI_TIET_PHIEU_MUON_KHONG_TON_TAI(404, "Không tìm thấy chi tiết phiếu mượn"),
    PHIEU_PHAT_KHONG_TON_TAI(404, "Không tìm thấy phiếu phạt này"),

    /**
     * <h1>500 - Bad Request
     * <h1>
     */
    INVALID_INPUT(400, "Dữ liệu đầu vào không hợp lệ"),
    NGUOI_DUNG_CON_NO_PHAT(400, "Người dùng còn phiếu phạt chưa thanh toán"),
    PHIEU_MUON_DA_TRA(400, "Phiếu mượn này đã được trả"),
    PHIEU_MUON_DA_HUY(400, "Phiếu mượn này đã bị huỷ"),
    CHI_TIET_DA_TRA(400, "Cuốn sách này đã được trả trước đó"),
    VUOT_QUA_SO_LAN_GIA_HAN(400, "Đã vượt quá số lần gia hạn tối đa"),
    KHONG_THE_GIA_HAN_QUA_HAN(400, "Không thể gia hạn cuốn sách đã qu hạn"),
    PHIEU_PHAT_DA_THANH_TOAN(400, "Phiếu pha này đã được thanh toán"),
    /**
     * <h1>500 - Internal Server
     * <h1>
     */
    SERVER_ERROR(500, "Hệ thống đang gặp sự cố, vui lòng thử lại sau");
    private final int status;
    private final String message;
}
