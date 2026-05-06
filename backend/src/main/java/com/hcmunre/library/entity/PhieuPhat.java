package com.hcmunre.library.entity;

import com.hcmunre.library.enums.TrangThaiThanhToan;
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
public class PhieuPhat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maPhieuPhat;

    private Double soTienPhat;
    private String lyDoPhat;

    @Enumerated(EnumType.STRING)
    private TrangThaiThanhToan trangThaiThanhToan;

    private LocalDateTime ngayThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chi_tiet_phieu_muon")
    private ChiTietPhieuMuon chiTietPhieuMuon;
}
