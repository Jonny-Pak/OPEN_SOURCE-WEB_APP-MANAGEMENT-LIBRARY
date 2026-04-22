package com.hcmunre.library_backend.model.entity;

import java.math.BigDecimal;
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
@Table(name = "phieu_phat")
public class PhieuPhat {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "ma_phieu_phat", nullable = false, length = 50)
    private String maPhieuPhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chi_tiet_phieu_muon")
    private ChiTietPhieuMuon chiTietPhieuMuon;

    @Column(name = "so_tien_phat", precision = 12, scale = 2)
    private BigDecimal soTienPhat;

    @Column(name = "ly_do_phat", length = 1000)
    private String lyDoPhat;

    @Column(name = "trang_thai_thanh_toan", length = 50)
    private String trangThaiThanhToan;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}