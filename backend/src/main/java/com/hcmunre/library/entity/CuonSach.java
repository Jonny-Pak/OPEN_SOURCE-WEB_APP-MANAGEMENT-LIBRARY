package com.hcmunre.library.entity;

import com.hcmunre.library.enums.TrangThaiCuonSach;
import com.hcmunre.library.enums.TinhTrangVatLy;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class CuonSach extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maCuonSach;

    @NotBlank(message = "Mã vạch không được để trống")
    @Column(unique = true, nullable = false)
    private String maVach;

    private String viTriKe;

    @NotNull(message = "Trạng thái cuốn sách không được để trống")
    @Enumerated(EnumType.STRING)
    private TrangThaiCuonSach trangThai;

    @Enumerated(EnumType.STRING)
    private TinhTrangVatLy tinhTrangVatLy;

    @Column(columnDefinition = "TEXT")
    private String ghiChuBaoTri;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach")
    private Sach sach;
}
