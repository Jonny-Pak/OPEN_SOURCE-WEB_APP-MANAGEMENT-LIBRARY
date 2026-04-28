package com.hcmunre.library.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Lỗi không tìm thấy cuốn sách
    SACH_KHONG_TON_TAI(404, "Không tìm thấy thông tin cuốn sách này"),

    // Lỗi không tìm thấy nhà xuất bản
    NHA_XUAT_BAN_KHONG_TON_TAI(404, "Không tìm thấy thông tin nhà xuất bản này"),

    // Lỗi không tìm thấy thể loại
    THE_LOAI_KHONG_TON_TAI(404, "Không tìm thấy thông tin thể loại này"),

    // Lỗi không tìm thấy tác giả
    TAC_GIA_KHONG_TON_TAI(404, "Không tìm thấy thông tin tác giả này"),

    // Lỗi không tìm thấy cuốn sách (bản sao vật lý)
    CUON_SACH_KHONG_TON_TAI(404, "Không tìm thấy thông tin cuốn sách này"),

    // Lỗi không tìm thấy hình ảnh sách
    HINH_ANH_SACH_KHONG_TON_TAI(404, "Không tìm thấy thông tin hình ảnh này"),

    // Lỗi mã ISBN đã tồn tại trong hệ thống
    ISBN_DA_TON_TAI(409, "Mã ISBN này đã được sử dụng bởi một đầu sách khác"),

    // Lỗi mã vạch cuốn sách đã tồn tại
    MA_VACH_DA_TON_TAI(409, "Mã vạch này đã được gắn cho một cuốn sách khác"),

    // Lỗi email đã tồn tại
    EMAIL_DA_TON_TAI(409, "Địa chỉ email này đã được sử dụng"),

    // Lỗi số điện thoại đã tồn tại
    SDT_DA_TON_TAI(409, "Số điện thoại này đã được sử dụng"),

    // Lỗi người dùng đã có đơn đặt chỗ đang chờ cho cùng đầu sách
    DA_DAT_CHO(409, "Bạn đã có một lượt đặt chỗ đang chờ cho đầu sách này"),

    // Lỗi dữ liệu đầu vào không hợp lệ
    INVALID_INPUT(400, "Dữ liệu đầu vào không hợp lệ"),
    // Lỗi hệ thống mặc định
    SERVER_ERROR(500, "Hệ thống đang gặp sự cố, vui lòng thử lại sau");

    private final int status;
    private final String message;
}
