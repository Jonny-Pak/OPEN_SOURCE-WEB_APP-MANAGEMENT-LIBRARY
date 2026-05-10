package com.hcmunre.library.dto.request;

import com.hcmunre.library.enums.LoaiHinhAnh;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HinhAnhSachRequest {

    @NotBlank(message = "Đường dẫn hình ảnh không được để trống")
    private String duongDan;

    @NotNull(message = "Loại hình ảnh không được để trống")
    private LoaiHinhAnh loaiHinhAnh;

    @NotNull(message = "Thứ tự hiển thị không được để trống")
    @Min(value = 0, message = "Thứ tự hiển thị phải lớn hơn hoặc bằng 0")
    private Integer thuTuHienThi;

    @NotNull(message = "Mã sách không được để trống")
    private Long maSach;
}
