package com.hcmunre.library.dto.request;

import com.hcmunre.library.enums.LoaiHinhAnh;
import lombok.Data;

/**
 * DTO dùng cho PATCH /api/v1/hinh-anh-sach/{id}
 * Chỉ cập nhật loại ảnh và thứ tự hiển thị, không thay đổi file.
 */
@Data
public class HinhAnhSachUpdateRequest {

    private LoaiHinhAnh loaiHinhAnh;

    private Integer thuTuHienThi;
}
