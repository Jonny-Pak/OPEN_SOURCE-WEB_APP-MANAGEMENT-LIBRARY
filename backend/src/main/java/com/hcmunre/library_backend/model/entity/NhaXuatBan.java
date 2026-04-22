package com.hcmunre.library_backend.model.entity;

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
@Table(name = "nha_xuat_ban")
public class NhaXuatBan {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "ma_nxb", nullable = false, length = 50)
    private String maNxb;

    @Column(name = "ten_nxb", length = 255)
    private String tenNxb;

    @Column(name = "dia_chi", length = 255)
    private String diaChi;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}