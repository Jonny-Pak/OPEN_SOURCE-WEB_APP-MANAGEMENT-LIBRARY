package com.hcmunre.library.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NhaXuatBanResponse {
    private Long maNhaXuatBan;
    private String tenNhaXuatBan;
    private String diaChi;
    private String soDienThoai;
    private String email;
}
