package com.hcmunre.library.service;

import com.hcmunre.library.dto.response.ImportExcelResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface cho chức năng import dữ liệu từ file Excel.
 */
public interface ImportExcelService {

    /**
     * Import danh sách người dùng (độc giả) từ file Excel (.xlsx).
     * Cột: STT | Mã SV | Họ tên | Giới tính | Ngày sinh | Số điện thoại | CCCD |
     * Email
     *
     * @param file file Excel (.xlsx)
     * @return kết quả import (thành công, thất bại, chi tiết lỗi)
     */
    ImportExcelResponse importNguoiDung(MultipartFile file);

    /**
     * Import danh sách bản sao sách (cuốn sách) từ file Excel (.xlsx).
     * Cột: STT | Mã sách (ISBN hoặc ID) | Mã vạch | Vị trí kệ | Tình trạng vật lý |
     * Ghi chú
     *
     * @param file file Excel (.xlsx)
     * @return kết quả import
     */
    ImportExcelResponse importCuonSach(MultipartFile file);

    /**
     * Import danh sách đầu sách từ file Excel (.xlsx).
     * Cột: STT | ISBN | Tên sách | Tác giả | NXB | Thể loại | Năm XB | Số trang |
     *       Lần tái bản | Giá tiền | Phạt/ngày | Mô tả | Ảnh bìa trước | Ảnh bìa sau | Ảnh khác
     *
     * @param file file Excel (.xlsx)
     * @return kết quả import
     */
    ImportExcelResponse importSach(MultipartFile file);

    /**
     * Import danh sách đầu sách từ file ZIP chứa:
     *  - Một file Excel (.xlsx) với cấu trúc giống importSach
     *  - Thư mục images/ (hoặc root) chứa các file ảnh
     *
     * Cột ảnh (12, 13, 14) trong Excel ghi TÊN FILE ảnh (vd: bia_truoc.jpg).
     * Backend sẽ tìm ảnh trong ZIP, lưu vào uploads/ và tạo URL trả về.
     * Nếu cột ảnh chứa URL (bắt đầu bằng http), sẽ dùng trực tiếp.
     *
     * @param zipFile file ZIP (.zip)
     * @return kết quả import
     */
    ImportExcelResponse importSachTuZip(MultipartFile zipFile);
}
