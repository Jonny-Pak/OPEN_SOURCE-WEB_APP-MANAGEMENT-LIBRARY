package com.hcmunre.library.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TacGia extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maTacGia;

    @NotBlank(message = "Họ đệm không được để trống")
    @Column(nullable = false)
    private String hoDem;

    @NotBlank(message = "Tên không được để trống")
    @Column(nullable = false)
    private String ten;

    @Column(columnDefinition = "TEXT")
    private String tieuSu;

    private LocalDateTime ngayXoa;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToMany(mappedBy = "danhSachTacGia")
    private List<Sach> danhSachSach;
}
