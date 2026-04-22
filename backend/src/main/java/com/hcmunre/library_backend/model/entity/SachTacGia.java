package com.hcmunre.library_backend.model.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sach_tac_gia")
public class SachTacGia {

    @EmbeddedId
    private SachTacGiaId id;

    @MapsId("maSach")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_sach")
    private Sach sach;

    @MapsId("maTacGia")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tac_gia")
    private TacGia tacGia;
}