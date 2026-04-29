package com.hcmunre.library.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class LichSuGiaHan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maLichSuGiaHan;

    private LocalDateTime ngayThucHien;
    private LocalDateTime hanTraCu;
    private LocalDateTime hanTraMoi;
    private String lyDo;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_thuc_hien")
    private NguoiDung nguoiThucHien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chi_tiet_phieu_muon")
    private ChiTietPhieuMuon chiTietPhieuMuon;
}
