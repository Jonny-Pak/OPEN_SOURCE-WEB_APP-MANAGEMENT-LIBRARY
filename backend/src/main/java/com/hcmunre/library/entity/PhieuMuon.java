package com.hcmunre.library.entity;

import com.hcmunre.library.enums.TrangThaiPhieuMuon;
import jakarta.persistence.*;
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
public class PhieuMuon extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID maPhieuMuon;

    private LocalDateTime ngayMuon;

    @Enumerated(EnumType.STRING)
    private TrangThaiPhieuMuon trangThaiPhieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung")
    private NguoiDung nguoiDung;

    @OneToMany(mappedBy = "phieuMuon", cascade = CascadeType.ALL)
    private List<ChiTietPhieuMuon> danhSachChiTietPhieuMuon;
}
