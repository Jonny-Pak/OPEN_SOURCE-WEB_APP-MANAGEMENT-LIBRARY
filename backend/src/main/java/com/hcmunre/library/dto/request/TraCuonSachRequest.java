package com.hcmunre.library.dto.request;

import com.hcmunre.library.enums.TinhTrangVatLy;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TraCuonSachRequest {
    @NotNull(message = "Mã chi tiết phiếu mượn không được để trống")
    private UUID maChiTietPhieuMuon;

    @NotNull(message = "Tình trạng sách lúc trả không được để trống")
    private TinhTrangVatLy tinhTrangLucTra;
}
