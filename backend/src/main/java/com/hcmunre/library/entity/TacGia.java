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
public class TacGia extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maTacGia;

    private String hoDem;
    private String ten;

    @Column(columnDefinition = "TEXT")
    private String tieuSu;

    private LocalDateTime ngayXoa;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToMany(mappedBy = "danhSachTacGia")
    private List<Sach> danhSachSach;
}
