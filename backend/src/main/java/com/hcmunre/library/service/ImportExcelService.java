package com.hcmunre.library.service;

import com.hcmunre.library.dto.response.ImportExcelResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface cho chức năng import dữ liệu từ file Excel.
 */
public interface ImportExcelService {

    /**
     * Import danh sách người dùng (độc giả) từ file Excel (.xlsx).
     *
     * @param file file Excel (.xlsx)
     * @return kết quả import (thành công, thất bại, chi tiết lỗi)
     */
    ImportExcelResponse importNguoiDung(MultipartFile file);
}
