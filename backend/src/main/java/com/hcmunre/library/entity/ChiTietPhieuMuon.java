package com.hcmunre.library.entity;

import com.hcmunre.library.enums.TinhTrangVatLy;
import com.hcmunre.library.enums.TrangThaiChiTietPhieuMuon;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ChiTietPhieuMuon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maChiTietPhieuMuon;

    @DecimalMin(value = "0.0", inclusive = true, message = "Đơn giá phạt áp dụng phải >= 0")
    private Double donGiaPhatApDung;

    @Enumerated(EnumType.STRING)
    private TinhTrangVatLy tinhTrangLucMuon;

    private LocalDateTime hanTraBanDau;
    private LocalDateTime hanTraHienTai;
    @Min(value = 0, message = "Số lần gia hạn phải lớn hơn hoặc bằng 0")
    private Integer soLanGiaHan;
    private LocalDateTime ngayTraThucTe;

    @Enumerated(EnumType.STRING)
    private TinhTrangVatLy tinhTrangLucTra;

    @Enumerated(EnumType.STRING)
    private TrangThaiChiTietPhieuMuon trangThaiChiTietPhieuMuon;

    @NotNull(message = "Phiếu mượn không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phieu_muon", nullable = false)
    private PhieuMuon phieuMuon;

    @NotNull(message = "Cuốn sách không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_cuon_sach", nullable = false)
    private CuonSach cuonSach;
}
