package com.hcmunre.library.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /** 404 - Not Found */
    SACH_KHONG_TON_TAI(404, "Không tìm thấy thông tin đầu sách này"),
    NHA_XUAT_BAN_KHONG_TON_TAI(404, "Không tìm thấy thông tin nhà xuất bản này"),
    THE_LOAI_KHONG_TON_TAI(404, "Không tìm thấy thông tin thể loại này"),
    TAC_GIA_KHONG_TON_TAI(404, "Không tìm thấy thông tin tác giả này"),
    CUON_SACH_KHONG_TON_TAI(404, "Không tìm thấy thông tin cuốn sách này"),
    HINH_ANH_SACH_KHONG_TON_TAI(404, "Không tìm thấy thông tin hình ảnh này"),
    PHIEU_MUON_KHONG_TON_TAI(404, "Không tìm thấy phiếu mượn này"),
    CHI_TIET_PHIEU_MUON_KHONG_TON_TAI(404, "Không tìm thấy chi tiết phiếu mượn"),
    PHIEU_PHAT_KHONG_TON_TAI(404, "Không tìm thấy phiếu phạt này"),

    /** 400 - Bad Request */
    INVALID_INPUT(400, "Dữ liệu đầu vào không hợp lệ"),
    NGUOI_DUNG_CON_NO_PHAT(400, "Người dùng còn phiếu phạt chưa thanh toán"),
    PHIEU_MUON_DA_TRA(400, "Phiếu mượn này đã được trả"),
    PHIEU_MUON_DA_HUY(400, "Phiếu mượn này đã bị huỷ"),
    CHI_TIET_DA_TRA(400, "Cuốn sách này đã được trả trước đó"),
    VUOT_QUA_SO_LAN_GIA_HAN(400, "Đã vượt quá số lần gia hạn tối đa"),
    KHONG_THE_GIA_HAN_QUA_HAN(400, "Không thể gia hạn cuốn sách đã quá hạn"),
    PHIEU_PHAT_DA_THANH_TOAN(400, "Phiếu phạt này đã được thanh toán"),

    /** 409 - Conflict */
    ISBN_DA_TON_TAI(409, "Mã ISBN này đã được sử dụng bởi một đầu sách khác"),
    MA_VACH_DA_TON_TAI(409, "Mã vạch này đã được gắn cho một cuốn sách khác"),
    EMAIL_DA_TON_TAI(409, "Địa chỉ email này đã được sử dụng"),
    SDT_DA_TON_TAI(409, "Số điện thoại này đã được sử dụng"),
    DA_DAT_CHO(409, "Bạn đã có một lượt đặt chỗ đang chờ cho đầu sách này"),

    /** 500 - Internal Server Error */
    SERVER_ERROR(500, "Hệ thống đang gặp sự cố, vui lòng thử lại sau");

    private final int status;
    private final String message;
}
