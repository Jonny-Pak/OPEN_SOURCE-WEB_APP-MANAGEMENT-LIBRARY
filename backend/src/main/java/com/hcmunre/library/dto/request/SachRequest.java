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
    @Min(value = 1000, message = "Năm xuất bản phải hợp lệ")
    private Integer namXuatBan;

    @Min(value = 1, message = "Lần tái bản phải là số nguyên dương (> 0)")
    private Integer lanTaiBan;

    @Min(value = 1, message = "Số trang phải là số nguyên dương (> 0)")
    private Integer soTrang;

    private String ngonNgu;

    private String moTa;

    @DecimalMin(value = "0.0", inclusive = true, message = "Đơn giá phạt theo ngày phải >= 0")
    private Double donGiaPhatTheoNgay;

    @NotNull(message = "Giá tiền sách không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá tiền sách phải >= 0")
    private Double giaTien;

    private String kichThuoc;
    private String dichGia;

    @NotNull(message = "Mã nhà xuất bản không được để trống")
    private Long maNhaXuatBan;
    
    @NotEmpty(message = "Danh sách thể loại không được để trống")
    private List<Long> maTheLoais;
    
    @NotEmpty(message = "Danh sách tác giả không được để trống")
    private List<Long> maTacGias;
}
