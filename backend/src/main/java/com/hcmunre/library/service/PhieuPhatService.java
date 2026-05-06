package com.hcmunre.library.service;

import com.hcmunre.library.dto.response.PhieuPhatResponse;
import com.hcmunre.library.entity.PhieuPhat;

import java.util.List;
import java.util.UUID;

public interface PhieuPhatService {
    // Queries
    List<PhieuPhatResponse> getPhieuPhatByChiTiet(UUID maChiTietPhieuMuon);
    boolean hasPhieuPhatUnpaid(UUID maNguoiDung);
    List<PhieuPhat> getAllPhieuPhat();
    PhieuPhat getPhieuPhatById(UUID id);

    // Commands
    PhieuPhatResponse createPhieuPhat(UUID maChiTietPhieuMuon, Double tienPhat, String lyDoPhat);
    PhieuPhatResponse payPhieuPhat(UUID maPhieuPhat);
    PhieuPhat createPhieuPhat(PhieuPhat phieuPhat);
    PhieuPhat updatePhieuPhat(UUID id, PhieuPhat phieuPhat);
    void deletePhieuPhat(UUID id);
}
