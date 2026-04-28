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
public class TheLoai extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maTheLoai;

    private String tenTheLoai;
    private String moTa;
    private LocalDateTime ngayXoa;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToMany(mappedBy = "danhSachTheLoai")
    private List<Sach> danhSachSach;
}
