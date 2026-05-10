package com.hcmunre.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class NhaXuatBan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maNhaXuatBan;

    @NotBlank(message = "Tên nhà xuất bản không được để trống")
    private String tenNhaXuatBan;

    private String diaChi;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0")
    @Column(unique = true)
    private String soDienThoai;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Column(unique = true)
    private String email;

    private LocalDateTime ngayXoa;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "nhaXuatBan")
    private List<Sach> danhSachSach;
}
