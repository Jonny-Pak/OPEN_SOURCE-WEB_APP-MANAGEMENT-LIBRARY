package com.hcmunre.library.service;

import com.hcmunre.library.entity.NguoiDung;

import java.util.UUID;

public interface NguoiDungService {
    NguoiDung getNguoiDungActive(UUID maNugoiDung);

    void checkNguoiDungPenalty(UUID maNguoiDung);
}
