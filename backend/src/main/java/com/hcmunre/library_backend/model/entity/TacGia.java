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
@Table(name = "tac_gia")
public class TacGia {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "ma_tac_gia", nullable = false, length = 50)
    private String maTacGia;

    @Column(name = "ho_dem", length = 100)
    private String hoDem;

    @Column(name = "ten", length = 50)
    private String ten;

    @Column(name = "tieu_su", length = 2000)
    private String tieuSu;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}