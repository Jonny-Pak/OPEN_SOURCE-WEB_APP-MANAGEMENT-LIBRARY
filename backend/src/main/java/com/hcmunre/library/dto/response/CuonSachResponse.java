package com.hcmunre.library.dto.response;

import com.hcmunre.library.enums.TinhTrangVatLy;
import com.hcmunre.library.enums.TrangThaiCuonSach;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CuonSachResponse {
    private Long maCuonSach;
    private Long maSach;
    private String tenSach;
    private String maVach;
    private String viTriKe;
    private TrangThaiCuonSach trangThai;
    private TinhTrangVatLy tinhTrangVatLy;
    private String ghiChuBaoTri;
}
