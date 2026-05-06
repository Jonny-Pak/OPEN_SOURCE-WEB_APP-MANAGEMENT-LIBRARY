package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class SachRequest {

    @NotBlank(message = "Tên sách không được để trống")
    private String tenSach;

    @NotBlank(message = "Mã ISBN không được để trống")
    @Pattern(
        regexp = "^(\\d{9}[\\dX]|\\d{13})$",
        message = "Mã ISBN phải là ISBN-10 (10 ký tự) hoặc ISBN-13 (13 chữ số)"
    )
    private String maIsbn;

    @Max(value = 9999, message = "Năm xuất bản không hợp lệ")
    private Integer namXuatBan;

    private Integer lanTaiBan;

    @Min(value = 1, message = "Số trang phải là số nguyên dương (> 0)")
    private Integer soTrang;

    private String ngonNgu;

    private String moTa;

    @DecimalMin(value = "0.0", inclusive = true, message = "Đơn giá phạt theo ngày phải >= 0")
    private Double donGiaPhatTheoNgay;

    private String kichThuoc;
    private String dichGia;

    private Long maNhaXuatBan;
    private List<Long> maTheLoais;
    private List<Long> maTacGias;
}
