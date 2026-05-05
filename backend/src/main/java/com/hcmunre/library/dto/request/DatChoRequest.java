package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DatChoRequest {

    @NotNull(message = "Phải chỉ định mã đầu sách")
    private Long maSach;

    @NotNull(message = "Phải chỉ định mã người dùng")
    private UUID maNguoiDung;

    @NotNull(message = "Thời gian đặt chỗ không được để trống")
    @FutureOrPresent(message = "Thời gian đặt chỗ phải là hiện tại hoặc trong tương lai")
    private LocalDateTime thoiGianDatCho;

    @NotNull(message = "Hạn giữ chỗ không được để trống")
    private LocalDateTime hanGiuCho;

    private String ghiChuHuy;
}
