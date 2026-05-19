package com.hcmunre.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@SQLRestriction("ngay_xoa IS NULL")
public class Sach extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maSach;

    @NotBlank(message = "Tên sách không được để trống")
    private String tenSach;
    // Giữ unique constraint ở DB nhưng bỏ Bean Validation để cho phép import với ISBN tự sinh
    @Column(unique = true, nullable = false)
    private String maIsbn;
    @Min(value = 1, message = "Số trang phải là số nguyên dương")
    private Integer soTrang;

    @Min(value = 1, message = "Lần tái bản phải là số nguyên dương")
    private Integer lanTaiBan;
    @Max(value = 9999, message = "Năm xuất bản không hợp lệ")
    @Min(value = 1000, message = "Năm xuất bản phải hợp lệ")
    private Integer namXuatBan;
    private String kichThuoc;
    private String dichGia;
    @NotNull(message = "Giá tiền không được để trống")
    @DecimalMin(value = "0.0", message = "Giá tiền phải >= 0")
    @Column(name = "gia_tien")
    private Double giaTien;

    @DecimalMin(value = "0.0", message = "Đơn giá phạt phải >= 0")
    private Double donGiaPhatTheoNgay;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    private LocalDateTime ngayXoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nha_xuat_ban")
    private NhaXuatBan nhaXuatBan;

    @OneToMany(mappedBy = "sach")
    private List<CuonSach> danhSachCuonSach;

    @OneToMany(mappedBy = "sach", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<HinhAnhSach> danhSachHinhAnh;

    @ManyToMany
    @JoinTable(name = "sach_tac_gia", joinColumns = @JoinColumn(name = "ma_sach"), inverseJoinColumns = @JoinColumn(name = "ma_tac_gia"))
    private List<TacGia> danhSachTacGia;

    @ManyToMany
    @JoinTable(name = "sach_the_loai", joinColumns = @JoinColumn(name = "ma_sach"), inverseJoinColumns = @JoinColumn(name = "ma_the_loai"))
    private List<TheLoai> danhSachTheLoai;
}
