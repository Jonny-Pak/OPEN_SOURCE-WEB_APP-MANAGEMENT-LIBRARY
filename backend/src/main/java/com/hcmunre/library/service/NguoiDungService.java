package com.hcmunre.library.service;

import com.hcmunre.library.entity.NguoiDung;

import java.util.UUID;

public interface NguoiDungService {
    // Queries
    NguoiDung getNguoiDungActive(UUID maNugoiDung);

    // Commands
    void checkNguoiDungPenalty(UUID maNguoiDung);
}
