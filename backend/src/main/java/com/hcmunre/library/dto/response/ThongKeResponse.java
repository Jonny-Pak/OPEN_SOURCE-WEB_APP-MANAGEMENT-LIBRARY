package com.hcmunre.library.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThongKeResponse {
    private long tongSoSach;
    private long tongSoCuonSach;
    private long tongSoNguoiDung;
    private long tongSoPhieuMuon;
}
