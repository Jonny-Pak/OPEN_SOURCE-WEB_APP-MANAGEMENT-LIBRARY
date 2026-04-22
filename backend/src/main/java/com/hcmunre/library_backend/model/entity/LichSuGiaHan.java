package com.hcmunre.library_backend.model.entity;

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
@Table(name = "lich_su_gia_han")
public class LichSuGiaHan {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "ma_lich_su_gia_han", nullable = false, length = 50)
    private String maLichSuGiaHan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chi_tiet_phieu_muon")
    private ChiTietPhieuMuon chiTietPhieuMuon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_thuc_hien")
    private NguoiDung nguoiThucHien;

    @Column(name = "ngay_thuc_hien")
    private LocalDateTime ngayThucHien;

    @Column(name = "han_tra_cu")
    private LocalDate hanTraCu;

    @Column(name = "han_tra_moi")
    private LocalDate hanTraMoi;

    @Column(name = "ly_do", length = 1000)
    private String lyDo;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
}