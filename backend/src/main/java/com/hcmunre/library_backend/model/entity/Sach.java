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
@Table(name = "sach")
public class Sach {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "ma_sach", nullable = false, length = 50)
    private String maSach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nxb")
    private NhaXuatBan nhaXuatBan;

    @Column(name = "ten_sach", length = 255)
    private String tenSach;

    @Column(name = "ma_isbn", length = 50)
    private String maIsbn;

    @Column(name = "lan_tai_ban")
    private Integer lanTaiBan;

    @Column(name = "so_trang")
    private Integer soTrang;

    @Column(name = "nam_xuat_ban")
    private Integer namXuatBan;

    @Column(name = "kich_thuoc", length = 100)
    private String kichThuoc;

    @Column(name = "dung_gia", length = 100)
    private String dungGia;

    @Column(name = "don_gia_phat_theo_ngay", precision = 12, scale = 2)
    private BigDecimal donGiaPhatTheoNgay;

    @Column(name = "mo_ta", length = 4000)
    private String moTa;

    @Column(name = "tong_so_luong")
    private Integer tongSoLuong;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}