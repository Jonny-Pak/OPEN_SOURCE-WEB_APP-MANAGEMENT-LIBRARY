package com.hcmunre.library_backend.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "chi_tiet_phieu_muon")
public class ChiTietPhieuMuon {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "ma_chi_tiet_phieu_muon", nullable = false, length = 50)
    private String maChiTietPhieuMuon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phieu_muon")
    private PhieuMuon phieuMuon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_cuon_sach")
    private CuonSach cuonSach;

    @Column(name = "don_gia_phat_ap_dung", precision = 12, scale = 2)
    private BigDecimal donGiaPhatApDung;

    @Column(name = "tinh_trang_luc_muon", length = 100)
    private String tinhTrangLucMuon;

    @Column(name = "han_tra_ban_dau")
    private LocalDate hanTraBanDau;

    @Column(name = "han_tra_hien_tai")
    private LocalDate hanTraHienTai;

    @Column(name = "so_lan_gia_han")
    private Integer soLanGiaHan;

    @Column(name = "ngay_tra_thuc_te")
    private LocalDateTime ngayTraThucTe;

    @Column(name = "tinh_trang_luc_tra", length = 100)
    private String tinhTrangLucTra;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}