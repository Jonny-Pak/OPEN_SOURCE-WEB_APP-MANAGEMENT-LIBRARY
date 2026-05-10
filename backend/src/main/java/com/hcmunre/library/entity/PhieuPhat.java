package com.hcmunre.library.entity;

import com.hcmunre.library.enums.TrangThaiThanhToan;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class PhieuPhat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maPhieuPhat;

    @NotNull(message = "Số tiền phạt không được để trống")
    @Min(value = 0, message = "Số tiền phạt không được âm")
    @Column(nullable = false)
    private Double soTienPhat;

    @NotBlank(message = "Lý do phạt không được để trống")
    @Column(nullable = false)
    private String lyDoPhat;

    @Enumerated(EnumType.STRING)
    private TrangThaiThanhToan trangThaiThanhToan;

    private LocalDateTime ngayThanhToan;

    @NotNull(message = "Chi tiết phiếu mượn không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chi_tiet_phieu_muon")
    private ChiTietPhieuMuon chiTietPhieuMuon;
}
