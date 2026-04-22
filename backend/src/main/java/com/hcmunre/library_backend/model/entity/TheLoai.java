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
@Table(name = "the_loai")
public class TheLoai {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "ma_the_loai", nullable = false, length = 50)
    private String maTheLoai;

    @Column(name = "ten_the_loai", length = 255)
    private String tenTheLoai;

    @Column(name = "mo_ta", length = 1000)
    private String moTa;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}