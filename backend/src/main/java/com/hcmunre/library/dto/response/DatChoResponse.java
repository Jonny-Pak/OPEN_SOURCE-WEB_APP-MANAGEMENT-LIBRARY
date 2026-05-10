package com.hcmunre.library.dto.response;

import com.hcmunre.library.enums.TrangThaiDatCho;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DatChoResponse {
    private UUID maDatCho;
    private Long maSach;
    private String tenSach;
    private UUID maNguoiDung;
    private String tenNguoiDung;
    private LocalDateTime thoiGianDatCho;
    private LocalDateTime hanGiuCho;
    private TrangThaiDatCho trangThaiDatCho;
    private String ghiChuHuy;
}
