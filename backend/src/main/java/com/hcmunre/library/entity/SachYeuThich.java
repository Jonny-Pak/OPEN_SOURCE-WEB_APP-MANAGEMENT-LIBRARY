package com.hcmunre.library.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"ma_nguoi_dung", "ma_sach"})})
public class SachYeuThich extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maSachYeuThich;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach", nullable = false)
    private Sach sach;
}
