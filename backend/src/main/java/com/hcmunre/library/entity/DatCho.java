package com.hcmunre.library.entity;

import com.hcmunre.library.enums.TrangThaiDatCho;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class DatCho extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maDatCho;

    private LocalDateTime thoiGianDatCho;
    private LocalDateTime hanGiuCho;

    @NotNull(message = "Trạng thái đặt chỗ không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiDatCho trangThai;

    private String ghiChuHuy;

    @NotNull(message = "Người dùng không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @NotNull(message = "Sách không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach", nullable = false)
    private Sach sach;
}
