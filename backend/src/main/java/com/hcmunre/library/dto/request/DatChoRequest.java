package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DatChoRequest {

    @NotNull(message = "Phải chỉ định mã đầu sách")
    private Long maSach;

    @NotNull(message = "Phải chỉ định mã người dùng")
    private UUID maNguoiDung;

    private String ghiChuHuy;
}
