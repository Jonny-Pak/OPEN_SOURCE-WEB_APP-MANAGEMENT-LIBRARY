package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.SachRequest;
import com.hcmunre.library.entity.CuonSach;
import com.hcmunre.library.entity.Sach;
import com.hcmunre.library.enums.TrangThaiCuonSach;

import java.util.List;

public interface SachService {
    // === CRUD (Book module) ===
    List<Sach> getAllSach();
    List<Sach> searchSach(String keyword);
    Sach getSachById(Long id);
    Sach createSach(SachRequest request);
    Sach updateSach(Long id, SachRequest request);
    void deleteSach(Long id);

    // === Borrowing System ===
    CuonSach getCuonSachAvailable(Long maSach);
    void updateTrangThaiCuonSach(Long maCuonSach, TrangThaiCuonSach trangThai);
}
