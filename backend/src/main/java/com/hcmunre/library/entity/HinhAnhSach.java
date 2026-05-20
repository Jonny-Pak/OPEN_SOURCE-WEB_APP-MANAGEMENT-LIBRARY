package com.hcmunre.library.entity;

import com.hcmunre.library.enums.LoaiHinhAnh;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class HinhAnhSach extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maHinhAnh;

    @NotBlank(message = "Đường dẫn hình ảnh không được để trống")
    @Column(nullable = false)
    private String duongDan;

    @NotNull(message = "Loại hình ảnh không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoaiHinhAnh loaiHinhAnh;

    @NotNull(message = "Thứ tự hiển thị không được để trống")
    @Min(value = 0, message = "Thứ tự hiển thị phải lớn hơn hoặc bằng 0")
    private Integer thuTuHienThi;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach")
    private Sach sach;
}
