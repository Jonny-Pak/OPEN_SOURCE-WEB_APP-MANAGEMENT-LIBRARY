package com.hcmunre.library.entity;

import com.hcmunre.library.enums.TrangThaiDatCho;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class DatCho extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maDatCho;

    private LocalDateTime thoiGianDatCho;
    private LocalDateTime hanGiuCho;

    @Enumerated(EnumType.STRING)
    private TrangThaiDatCho trangThai;

    private String ghiChuHuy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung")
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach")
    private Sach sach;
}
