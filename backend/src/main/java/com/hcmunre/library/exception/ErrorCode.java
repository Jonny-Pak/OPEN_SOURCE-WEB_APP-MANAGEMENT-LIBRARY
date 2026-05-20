package com.hcmunre.library.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /** 404 - Not Found */
    SACH_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin đầu sách này"),
    NHA_XUAT_BAN_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin nhà xuất bản này"),
    THE_LOAI_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin thể loại này"),
    TAC_GIA_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin tác giả này"),
    CUON_SACH_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin cuốn sách này"),
    HINH_ANH_SACH_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin hình ảnh này"),
    PHIEU_MUON_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu mượn này"),
    CHI_TIET_PHIEU_MUON_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy chi tiết phiếu mượn"),
    PHIEU_PHAT_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu phạt này"),
    DAT_CHO_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin đặt chỗ này"),

    /** 400 - Bad Request */
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "Dữ liệu đầu vào không hợp lệ"),
    FILE_TRONG(HttpStatus.BAD_REQUEST, "File tải lên không được để trống"),
    LOI_LUU_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra khi lưu trữ file"),
    NGUOI_DUNG_CON_NO_PHAT(HttpStatus.BAD_REQUEST, "Người dùng còn phiếu phạt chưa thanh toán"),
    SACH_DANG_QUA_HAN(HttpStatus.BAD_REQUEST, "Bạn đang có sách quá hạn chưa trả, không thể thực hiện giao dịch mới"),
    CUON_SACH_KHONG_SAN_SANG(HttpStatus.BAD_REQUEST, "Cuốn sách này hiện không sẵn sàng để mượn"),

    PHIEU_MUON_DA_HOAN_TAT(HttpStatus.BAD_REQUEST, "Phiếu mượn này đã được hoàn tất trước đó"),
    PHIEU_MUON_DA_HUY(HttpStatus.BAD_REQUEST, "Phiếu mượn này đã bị huỷ"),

    KHONG_THE_HUY_PHIEU_DANG_SU_DUNG(HttpStatus.BAD_REQUEST, "Không thể hủy phiếu mượn vì đã có sách được trả"),
    CHI_TIET_DA_TRA(HttpStatus.BAD_REQUEST, "Cuốn sách này đã được trả trước đó"),
    VUOT_QUA_SO_LAN_GIA_HAN(HttpStatus.BAD_REQUEST, "Đã vượt quá số lần gia hạn tối đa"),
    KHONG_THE_GIA_HAN_QUA_HAN(HttpStatus.BAD_REQUEST, "Không thể gia hạn cuốn sách đã quá hạn"),
    PHIEU_PHAT_DA_THANH_TOAN(HttpStatus.BAD_REQUEST, "Phiếu phạt này đã được thanh toán"),
    PHIEU_PHAT_DA_HUY(HttpStatus.BAD_REQUEST, "Phiếu phạt này đã bị huỷ"),
    VUOT_QUA_GIOI_HAN_MUON(HttpStatus.BAD_REQUEST, "Bạn đã vượt quá số lượng sách tối đa được phép mượn cùng lúc"),
    VUOT_QUA_GIOI_HAN_DAT_CHO(HttpStatus.BAD_REQUEST, "Bạn đã vượt quá số lượng sách tối đa được phép đặt chỗ cùng lúc"),
    KHONG_THE_BAO_MAT_SACH_DA_TRA(HttpStatus.BAD_REQUEST, "Không thể báo mất cuốn sách đã được trả"),

    DAT_CHO_DA_XU_LY(HttpStatus.BAD_REQUEST, "Lượt đặt chỗ này đã được xử lý hoặc hủy bỏ trước đó"),
    SACH_DA_CO_NGUOI_DAT_CHO(HttpStatus.BAD_REQUEST, "Sách này đã được độc giả khác đặt chỗ, không thể gia hạn"),

    MAT_KHAU_KHONG_KHOP(HttpStatus.BAD_REQUEST, "Mật khẩu mới và xác nhận mật khẩu không trùng khớp"),
    MAT_KHAU_CU_SAI(HttpStatus.BAD_REQUEST, "Mật khẩu cũ không chính xác"),

    SACH_DA_CO_TRONG_YEU_THICH(HttpStatus.BAD_REQUEST, "Cuốn sách này đã có trong danh sách yêu thích của bạn"),

    OTP_KHONG_HOP_LE(HttpStatus.BAD_REQUEST, "Mã OTP không hợp lệ hoặc đã hết hạn"),
    OTP_DA_HET_HAN(HttpStatus.BAD_REQUEST, "Mã OTP đã hết hạn, vui lòng yêu cầu mã mới"),

    FILE_EXCEL_KHONG_HOP_LE(HttpStatus.BAD_REQUEST, "File không đúng định dạng Excel (.xlsx)"),
    LOI_DOC_FILE_EXCEL(HttpStatus.BAD_REQUEST, "Có lỗi xảy ra khi đọc file Excel"),
    EXCEL_THIEU_HO_DEM(HttpStatus.BAD_REQUEST, "Họ đệm không được để trống"),
    EXCEL_THIEU_TEN(HttpStatus.BAD_REQUEST, "Tên không được để trống"),
    EXCEL_THIEU_EMAIL(HttpStatus.BAD_REQUEST, "Email không được để trống"),
    EXCEL_THIEU_SDT(HttpStatus.BAD_REQUEST, "Số điện thoại không được để trống"),
    EXCEL_NGAY_SINH_SAI_FORMAT(HttpStatus.BAD_REQUEST, "Ngày sinh không đúng định dạng dd/MM/yyyy"),
    EXCEL_GIOI_TINH_KHONG_HOP_LE(HttpStatus.BAD_REQUEST, "Giới tính không hợp lệ (NAM hoặc NU)"),

    /** 401 - Unauthorized */
    CHUA_DANG_NHAP(HttpStatus.UNAUTHORIZED, "Bạn chưa đăng nhập hoặc phiên đăng nhập đã hết hạn"),
    DANG_NHAP_THAT_BAI(HttpStatus.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không đúng"),

    /** 403 - Forbidden */
    KHONG_CO_QUYEN(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập tài nguyên này"),

    /** 404 - Not Found */
    NGUOI_DUNG_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"),
    THONG_BAO_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo này"),

    /** 409 - Conflict */
    ISBN_DA_TON_TAI(HttpStatus.CONFLICT, "Mã ISBN này đã được sử dụng bởi một đầu sách khác"),
    MA_VACH_DA_TON_TAI(HttpStatus.CONFLICT, "Mã vạch này đã được gắn cho một cuốn sách khác"),
    EMAIL_DA_TON_TAI(HttpStatus.CONFLICT, "Địa chỉ email này đã được sử dụng"),
    SDT_DA_TON_TAI(HttpStatus.CONFLICT, "Số điện thoại này đã được sử dụng"),
    CCCD_DA_TON_TAI(HttpStatus.CONFLICT, "Số CCCD này đã được sử dụng"),
    CCCD_KHONG_HOP_LE(HttpStatus.BAD_REQUEST, "Số CCCD phải đúng 12 chữ số"),
    EMAIL_KHONG_HOP_LE(HttpStatus.BAD_REQUEST, "Địa chỉ email không đúng định dạng"),
    SDT_KHONG_HOP_LE(HttpStatus.BAD_REQUEST, "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0"),
    MA_VACH_TRONG(HttpStatus.BAD_REQUEST, "Mã vạch cuốn sách không được để trống"),
    DA_DAT_CHO(HttpStatus.CONFLICT, "Bạn đã có một lượt đặt chỗ đang chờ cho đầu sách này"),

    SACH_YEU_THICH_KHONG_TON_TAI(HttpStatus.NOT_FOUND, "Không tìm thấy sách trong danh sách yêu thích"),

    /** 423 - Locked */
    TAI_KHOAN_BI_KHOA(HttpStatus.LOCKED, "Tài khoản của bạn đã bị khóa"),
    TAI_KHOAN_CHUA_KICH_HOAT(HttpStatus.UNAUTHORIZED, "Tài khoản của bạn chưa được kích hoạt"),

    /** 500 - Internal Server Error */
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Hệ thống đang gặp sự cố, vui lòng thử lại sau");

    private final HttpStatus httpStatus;
    private final String message;
}
