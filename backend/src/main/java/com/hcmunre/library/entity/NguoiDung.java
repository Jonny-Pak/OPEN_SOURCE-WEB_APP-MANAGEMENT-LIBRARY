package com.hcmunre.library.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.hcmunre.library.enums.GioiTinh;

import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.enums.VaiTro;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past;
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

    @NotBlank(message = "Họ đệm không được để trống")
    @Column(nullable = false)
    private String hoDem;

    @NotBlank(message = "Tên không được để trống")
    @Column(nullable = false)
    private String ten;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0")
    @Column(unique = true)
    private String soDienThoai;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(nullable = false)
    private String matKhau;
    @Past(message = "Ngày sinh phải trong quá khứ")
    private LocalDate ngaySinh;

    @Enumerated(EnumType.STRING)
    private GioiTinh gioiTinh;

    @Pattern(regexp = "^\\d{12}$", message = "CCCD phải bao gồm 12 chữ số")
    private String cccd;
    private String diaChi;

    @Enumerated(EnumType.STRING)
    private VaiTro vaiTro;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private TrangThaiNguoiDung trangThai;

    private LocalDateTime ngayXoa;

    @OneToMany(mappedBy = "nguoiDung")
    private List<PhieuMuon> danhSachPhieuMuon;

    @OneToMany(mappedBy = "nguoiDung")
    private List<DatCho> danhSachDatCho;

    public String getHoTen(){
        return this.hoDem + " " + this.getTen();
    }
}
