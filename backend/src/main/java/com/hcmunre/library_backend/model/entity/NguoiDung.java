package com.hcmunre.library_backend.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "nguoi_dung")
public class NguoiDung {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "ma_nguoi_dung", nullable = false, length = 50)
    private String maNguoiDung;

    @Column(name = "ho_dem", length = 100)
    private String hoDem;

    @Column(name = "ten", length = 50)
    private String ten;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @Column(name = "mat_khau", length = 255)
    private String matKhau;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh", length = 20)
    private String gioiTinh;

    @Column(name = "cccd", length = 20)
    private String cccd;

    @Column(name = "dia_chi", length = 255)
    private String diaChi;

    @Column(name = "vai_tro", length = 50)
    private String vaiTro;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}