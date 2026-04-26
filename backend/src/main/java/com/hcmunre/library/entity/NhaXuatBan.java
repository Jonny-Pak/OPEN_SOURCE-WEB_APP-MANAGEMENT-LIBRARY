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
public class NhaXuatBan extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maNhaXuatBan;

    private String tenNhaXuatBan;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private LocalDateTime ngayXoa;

    @OneToMany(mappedBy = "nhaXuatBan")
    private List<Sach> danhSachSach;
}
