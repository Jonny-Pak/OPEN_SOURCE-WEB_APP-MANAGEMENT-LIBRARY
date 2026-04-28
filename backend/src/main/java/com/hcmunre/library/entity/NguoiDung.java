package com.hcmunre.library.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.hcmunre.library.enums.GioiTinh;
import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.enums.VaiTro;
import com.hcmunre.library.validator.MinAge;
import com.hcmunre.library.validator.ValidCccd;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Họ đệm không được trống")
    @Column(nullable = false)
    private String hoDem;

    @NotBlank(message = "Tên không được trống")
    @Column(nullable = false)
    private String ten;

    @NotBlank(message = "Email không được trống")
    @Email(message = "Email không hợp lệ")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Số điện thoại không được trống")
    @Pattern(regexp = "^(\\+84|0)[0-9]{9}$", message = "Số điện thoại phải bắt đầu bằng +84 hoặc 0 và có 10 chữ số")
    @Column(nullable = false, unique = true)
    private String soDienThoai;

    @NotBlank(message = "Mật khẩu không được trống")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    @Column(nullable = false)
    private String matKhau;

    @NotNull(message = "Ngày sinh không được trống")
    @MinAge(value = 15, message = "Người dùng phải >= 15 tuổi")
    @Column(nullable = false)
    private LocalDate ngaySinh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GioiTinh gioiTinh;

    @NotBlank(message = "CCCD không được trống")
    @ValidCccd(message = "CCCD phải có đúng 12 chữ số")
    @Column(nullable = false, unique = true)
    private String cccd;

    @Column(nullable = false)
    private String diaChi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VaiTro vaiTro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiNguoiDung trangThai;

    private LocalDateTime ngayXoa;

    @OneToMany(mappedBy = "nguoiDung")
    private List<PhieuMuon> danhSachPhieuMuon;

    @OneToMany(mappedBy = "nguoiDung")
    private List<DatCho> danhSachDatCho;
}
