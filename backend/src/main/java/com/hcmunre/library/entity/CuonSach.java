package com.hcmunre.library.entity;

import com.hcmunre.library.enums.TrangThaiCuonSach;
import com.hcmunre.library.enums.TinhTrangSach;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class CuonSach extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maCuonSach;

    private String maVach;
    private String viTriKe;

    @Enumerated(EnumType.STRING)
    private TrangThaiCuonSach trangThai;

    @Enumerated(EnumType.STRING)
    private TinhTrangSach tinhTrangVatLy;

    @Column(columnDefinition = "TEXT")
    private String ghiChuBaoTri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach")
    private Sach sach;
}
