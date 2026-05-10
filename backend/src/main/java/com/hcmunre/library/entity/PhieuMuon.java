package com.hcmunre.library.entity;

import com.hcmunre.library.enums.TrangThaiPhieuMuon;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PhieuMuon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID maPhieuMuon;

    private LocalDateTime ngayMuon;

    @NotNull(message = "Trạng thái phiếu mượn không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiPhieuMuon trangThaiPhieu;

    @NotNull(message = "Người dùng không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @OneToMany(mappedBy = "phieuMuon", cascade = CascadeType.ALL)
    private List<ChiTietPhieuMuon> danhSachChiTietPhieuMuon;
}
