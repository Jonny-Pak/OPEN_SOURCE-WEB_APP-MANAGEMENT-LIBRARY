package com.hcmunre.library.dto.request;

import com.hcmunre.library.enums.TinhTrangVatLy;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class TraToanBoRequest {
    @NotNull(message = "Mã phiếu mượn không được để trống")
    private UUID maPhieuMuon;
    
    @NotNull(message = "Tình trạng lúc trả không được để trống")
    private TinhTrangVatLy tinhTrangLucTra;
}
