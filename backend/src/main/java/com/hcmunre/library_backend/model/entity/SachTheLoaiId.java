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
public class SachTheLoaiId implements Serializable {

    @Column(name = "ma_sach", length = 50)
    private String maSach;

    @Column(name = "ma_the_loai", length = 50)
    private String maTheLoai;
}