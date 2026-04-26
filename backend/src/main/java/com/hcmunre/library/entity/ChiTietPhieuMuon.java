package com.hcmunre.library.entity;

import com.hcmunre.library.enums.TinhTrangSach;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ChiTietPhieuMuon extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maChiTietPhieuMuon;

    private Double donGiaPhatApDung;

    @Enumerated(EnumType.STRING)
    private TinhTrangSach tinhTrangLucMuon;

    private LocalDateTime hanTraBanDau;
    private LocalDateTime hanTraHienTai;
    private Integer soLanGiaHan;
    private LocalDateTime ngayTraThucTe;

    @Enumerated(EnumType.STRING)
    private TinhTrangSach tinhTrangLucTra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phieu_muon")
    private PhieuMuon phieuMuon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_cuon_sach")
    private CuonSach cuonSach;
}
