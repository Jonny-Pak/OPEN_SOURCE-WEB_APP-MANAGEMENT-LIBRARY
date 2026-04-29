package com.hcmunre.library.dto.request;

import com.hcmunre.library.enums.TinhTrangSach;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TraSachRequest {
    @NotNull(message = "Mã chi tiết phiê mượn không được dê trống")
    private UUID maChiTietPhieuMuon;

    @NotNull(message = "Tình trạng sách lúc trả không được để trống")
    private TinhTrangSach tinhTrangLucTra;
}
