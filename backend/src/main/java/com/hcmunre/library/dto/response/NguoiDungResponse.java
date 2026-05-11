package com.hcmunre.library.dto.response;

import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.enums.VaiTro;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NguoiDungResponse {
    private UUID maNguoiDung;
    private String hoDem;
    private String ten;
    private String email;
    private String soDienThoai;
    private VaiTro vaiTro;
    private TrangThaiNguoiDung trangThai;
}
