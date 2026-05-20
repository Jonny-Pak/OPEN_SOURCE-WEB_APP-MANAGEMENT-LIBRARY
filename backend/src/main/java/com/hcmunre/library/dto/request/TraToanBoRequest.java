package com.hcmunre.library.dto.request;

import com.hcmunre.library.enums.TinhTrangVatLy;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraToanBoRequest {
    @NotNull(message = "Mã phiếu mượn không được để trống")
    private UUID maPhieuMuon;
    
    @NotNull(message = "Tình trạng lúc trả không được để trống")
    private TinhTrangVatLy tinhTrangLucTra;
}
