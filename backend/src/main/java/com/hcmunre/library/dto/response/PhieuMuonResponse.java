package com.hcmunre.library.dto.response;

import com.hcmunre.library.enums.TinhTrangVatLy;
import com.hcmunre.library.enums.TrangThaiChiTietPhieuMuon;
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
        private String tenSach;
        private LocalDateTime hanTraBanDau;
        private LocalDateTime hanTraHienTai;
        private LocalDateTime ngayTraThucTe;
        private TinhTrangVatLy tinhTrangLucMuon;
        private TinhTrangVatLy tinhTrangLucTra;
        private TrangThaiChiTietPhieuMuon trangThaiChiTietPhieuMuon;
        private String tenSach;
        private String anhBiaUrl;
        private Integer soLanGiaHan;
        private Double donGiaPhatApDung;

    }
}
