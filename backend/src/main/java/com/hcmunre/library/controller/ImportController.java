package com.hcmunre.library.controller;

import com.hcmunre.library.dto.response.ImportExcelResponse;
import com.hcmunre.library.service.ImportExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller xử lý các chức năng import dữ liệu từ file Excel.
 * Hỗ trợ:
 *  - Import danh sách người dùng / độc giả
 *  - Import danh sách đầu sách (thông tin metadata)
 *  - Import danh sách cuốn sách (bản sao vật lý)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/import/excel")
public class ImportController {

    private final ImportExcelService importExcelService;

    /**
     * Import danh sách người dùng (độc giả) từ file Excel.
     * Cột: STT | Mã SV | Họ tên | Giới tính | Ngày sinh | Số điện thoại | CCCD | Email
     */
    @PostMapping("/nguoi-dung")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<ImportExcelResponse> importNguoiDung(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(importExcelService.importNguoiDung(file));
    }

    /**
     * Import danh sách cuốn sách (bản sao) từ file Excel.
     * Cột: STT | Mã sách (ISBN hoặc ID) | Mã vạch | Vị trí kệ | Tình trạng vật lý | Ghi chú
     */
    @PostMapping("/cuon-sach")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<ImportExcelResponse> importCuonSach(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(importExcelService.importCuonSach(file));
    }

    /**
     * Import danh sách đầu sách từ file Excel.
     * Cột: STT | ISBN | Tên sách | Tác giả | NXB | Thể loại | Năm XB | Số trang |
     *       Lần tái bản | Giá tiền | Phạt/ngày | Mô tả | Ảnh bìa trước | Ảnh bìa sau | Ảnh khác
     */
    @PostMapping("/sach")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<ImportExcelResponse> importSach(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(importExcelService.importSach(file));
    }

    /**
     * Import danh sách đầu sách kèm hình ảnh từ file ZIP.
     *
     * Cấu trúc ZIP:
     *   sach.xlsx          ← file Excel (bắt buộc)
     *   images/            ← thư mục chứa ảnh (hoặc đặt thẳng vào root ZIP)
     *     bia_truoc.jpg
     *     bia_sau.jpg
     *     ...
     *
     * Cột Excel:
     *   (12) Ảnh bìa trước  → tên file ảnh (vd: bia_truoc.jpg) hoặc URL http://...
     *   (13) Ảnh bìa sau    → tên file ảnh hoặc URL
     *   (14) Ảnh khác       → tên file(s) cách nhau bằng | hoặc URL
     */
    @PostMapping(value = "/sach-zip", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<ImportExcelResponse> importSachVoiAnh(
            @RequestParam("file") MultipartFile zipFile) {
        return ResponseEntity.ok(importExcelService.importSachTuZip(zipFile));
    }
}
