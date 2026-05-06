package com.hcmunre.library.service;

import com.hcmunre.library.dto.response.PhieuPhatResponse;
import com.hcmunre.library.entity.PhieuPhat;

import java.util.List;
import java.util.UUID;

public interface PhieuPhatService {
    // === Borrowing System ===
    PhieuPhatResponse createPhieuPhat(UUID maChiTietPhieuMuon, Double tienPhat, String lyDoPhat);
    PhieuPhatResponse payPhieuPhat(UUID maPhieuPhat);
    List<PhieuPhatResponse> getPhieuPhatByChiTiet(UUID maChiTietPhieuMuon);
    boolean hasPhieuPhatUnpaid(UUID maNguoiDung);

    // === CRUD (Book module) ===
    List<PhieuPhat> getAllPhieuPhat();
    PhieuPhat getPhieuPhatById(UUID id);
    PhieuPhat createPhieuPhat(PhieuPhat phieuPhat);
    PhieuPhat updatePhieuPhat(UUID id, PhieuPhat phieuPhat);
    void deletePhieuPhat(UUID id);
}
