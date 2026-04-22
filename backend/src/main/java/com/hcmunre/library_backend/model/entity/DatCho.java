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
@Table(name = "dat_cho")
public class DatCho {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "ma_dat_cho", nullable = false, length = 50)
    private String maDatCho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung")
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach")
    private Sach sach;

    @Column(name = "thoi_gian_dat_cho")
    private LocalDateTime thoiGianDatCho;

    @Column(name = "han_giu_cho")
    private LocalDateTime hanGiuCho;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "ghi_chu_huy", length = 1000)
    private String ghiChuHuy;
}