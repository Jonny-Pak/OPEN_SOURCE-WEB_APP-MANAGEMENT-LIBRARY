package com.hcmunre.library.entity;

import com.hcmunre.library.enums.TrangThaiEmail;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class EmailOutbox extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID maEmail;

    @Column(nullable = false)
    private String emailNguoiNhan;

    @Column(nullable = false)
    private String tieuDe;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiEmail trangThai;

    @Column(nullable = false)
    private int soLanThu;

    @Column(columnDefinition = "TEXT")
    private String loi;
}
