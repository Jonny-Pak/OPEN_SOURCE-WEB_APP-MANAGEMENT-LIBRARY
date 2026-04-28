package com.hcmunre.library.entity;

import com.hcmunre.library.enums.LoaiHinhAnh;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class HinhAnhSach extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maHinhAnh;

    private String duongDan;

    @Enumerated(EnumType.STRING)
    private LoaiHinhAnh loaiHinhAnh;

    private Integer thuTuHienThi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach")
    private Sach sach;
}
