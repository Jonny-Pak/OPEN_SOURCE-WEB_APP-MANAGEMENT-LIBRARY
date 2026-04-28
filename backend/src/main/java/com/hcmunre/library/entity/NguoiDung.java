package com.hcmunre.library.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.hcmunre.library.enums.GioiTinh;

import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.enums.VaiTro;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class NguoiDung extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maNguoiDung;

    private String hoDem;
    private String ten;
    private String email;
    private String soDienThoai;
    private String matKhau;
    private LocalDate ngaySinh;

    @Enumerated(EnumType.STRING)
    private GioiTinh gioiTinh;

    private String cccd;
    private String diaChi;

    @Enumerated(EnumType.STRING)
    private VaiTro vaiTro;

    @Enumerated(EnumType.STRING)
    private TrangThaiNguoiDung trangThai;

    private LocalDateTime ngayXoa;

    @OneToMany(mappedBy = "nguoiDung")
    private List<PhieuMuon> danhSachPhieuMuon;

    @OneToMany(mappedBy = "nguoiDung")
    private List<DatCho> danhSachDatCho;
}
