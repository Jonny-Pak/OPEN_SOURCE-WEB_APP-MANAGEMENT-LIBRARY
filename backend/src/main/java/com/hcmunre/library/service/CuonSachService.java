package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.CuonSachRequest;
import com.hcmunre.library.entity.CuonSach;
import com.hcmunre.library.enums.TrangThaiCuonSach;

import java.util.List;

public interface CuonSachService {
    // Queries
    List<CuonSach> getAllCuonSach();
    List<CuonSach> getCuonSachBySach(Long maSach);
    CuonSach getCuonSachById(Long id);
    CuonSach getCuonSachAvailable(Long maSach);

    // Commands
    CuonSach createCuonSach(CuonSachRequest request);
    CuonSach updateCuonSach(Long id, CuonSachRequest request);
    void updateTrangThaiCuonSach(Long maCuonSach, TrangThaiCuonSach trangThai);
    void deleteCuonSach(Long id);
}
