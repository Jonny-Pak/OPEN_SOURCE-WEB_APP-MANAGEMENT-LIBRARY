package com.hcmunre.library_backend.model.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SachTacGiaId implements Serializable {

    @Column(name = "ma_sach", length = 50)
    private String maSach;

    @Column(name = "ma_tac_gia", length = 50)
    private String maTacGia;
}