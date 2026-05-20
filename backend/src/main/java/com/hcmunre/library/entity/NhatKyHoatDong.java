package com.hcmunre.library.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "nhat_ky_hoat_dong")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhatKyHoatDong {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID maNguoiDung;
    private String hoTen;
    private String vaiTro;
    private String hanhDong;

    @Column(columnDefinition = "TEXT")
    private String chiTiet;

    private LocalDateTime thoiGian;

    @PrePersist
    protected void onCreate() {
        thoiGian = LocalDateTime.now();
    }
}
