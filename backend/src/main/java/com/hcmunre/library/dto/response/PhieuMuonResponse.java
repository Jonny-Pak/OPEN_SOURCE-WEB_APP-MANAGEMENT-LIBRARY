package com.hcmunre.library.dto.response;

import com.hcmunre.library.enums.TinhTrangSach;
import com.hcmunre.library.enums.TrangThaiPhieuMuon;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuMuonResponse {
    private UUID maPhieuMuon;
    private UUID maNguoiDung;
    private String tenDocGia;
    private LocalDateTime ngayMuon;
    private TrangThaiPhieuMuon trangThaiPhieu;
    private List<ChitietResponse> danhSachChiTiet;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChitietResponse{
        private UUID maChiTietPhieuMuon;
        private Long maCuonSach;
        private String maVach;
        private LocalDateTime hanTraBanDau;
        private LocalDateTime hanTraHienTai;
        private LocalDateTime ngayTraThucTe;
        private TinhTrangSach tinhTrangLucMuon;
        private TinhTrangSach tinhTrangLucTra;
        private Integer soLanGiaHan;
        private Double donGiaPhatApDung;

    }
}
