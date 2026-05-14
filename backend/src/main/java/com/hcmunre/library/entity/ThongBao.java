package com.hcmunre.library.entity;

import com.hcmunre.library.enums.LoaiThongBao;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ThongBao extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID maThongBao;

    @Column(nullable = false)
    private String tieuDe;

    @Column(nullable = false)
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoaiThongBao loaiThongBao;

    private boolean daDoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;
}
