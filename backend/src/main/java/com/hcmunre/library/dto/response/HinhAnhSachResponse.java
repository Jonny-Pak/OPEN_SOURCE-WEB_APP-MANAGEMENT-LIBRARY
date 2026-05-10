package com.hcmunre.library.dto.response;

import com.hcmunre.library.enums.LoaiHinhAnh;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HinhAnhSachResponse {
    private Long maHinhAnh;
    private String duongDan;
    private LoaiHinhAnh loaiHinhAnh;
    private Integer thuTuHienThi;
    private Long maSach;
}
