package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class TaoPhieuPhatRequest {
    @NotNull(message = "Phải có mã chi tiết phiếu mượn")
    private UUID maChiTietPhieuMuon;
    
    @NotNull(message = "Số tiền phạt không được để trống")
    @Min(value = 0, message = "Số tiền phạt không được âm")
    private Double soTienPhat;
    
    @NotBlank(message = "Phải có lý do phạt")
    private String lyDoPhat;
}
