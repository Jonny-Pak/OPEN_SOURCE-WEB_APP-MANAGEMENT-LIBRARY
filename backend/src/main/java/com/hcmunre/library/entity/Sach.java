package com.hcmunre.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Sach extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maSach;

    private String tenSach;
    private String maIsbn;
    private Integer lanTaiBan;
    private Integer soTrang;
    private Integer namXuatBan;
    private String kichThuoc;
    private String dichGia;
    private Double donGiaPhatTheoNgay;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    private LocalDateTime ngayXoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nha_xuat_ban")
    private NhaXuatBan nhaXuatBan;

    @OneToMany(mappedBy = "sach")
    private List<CuonSach> danhSachCuonSach;

    @OneToMany(mappedBy = "sach", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HinhAnhSach> danhSachHinhAnh;

    @ManyToMany
    @JoinTable(name = "sach_tac_gia", joinColumns = @JoinColumn(name = "ma_sach"), inverseJoinColumns = @JoinColumn(name = "ma_tac_gia"))
    private List<TacGia> danhSachTacGia;

    @ManyToMany
    @JoinTable(name = "sach_the_loai", joinColumns = @JoinColumn(name = "ma_sach"), inverseJoinColumns = @JoinColumn(name = "ma_the_loai"))
    private List<TheLoai> danhSachTheLoai;
}
