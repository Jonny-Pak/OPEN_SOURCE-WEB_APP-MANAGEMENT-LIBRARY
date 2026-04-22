package com.hcmunre.library_backend.model.entity;

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
@Table(name = "cuon_sach")
public class CuonSach {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "ma_cuon_sach", nullable = false, length = 50)
    private String maCuonSach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach")
    private Sach sach;

    @Column(name = "ma_vach", length = 100)
    private String maVach;

    @Column(name = "vi_tri_ke", length = 255)
    private String viTriKe;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "tinh_trang_vat_ly", length = 100)
    private String tinhTrangVatLy;

    @Column(name = "chi_chu_bao_tri", length = 1000)
    private String chiChuBaoTri;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}