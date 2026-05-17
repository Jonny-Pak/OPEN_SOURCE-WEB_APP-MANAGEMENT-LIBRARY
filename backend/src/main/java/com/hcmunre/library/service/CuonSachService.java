package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.CuonSachRequest;
import com.hcmunre.library.dto.response.CuonSachResponse;
import com.hcmunre.library.entity.CuonSach;
import com.hcmunre.library.enums.TrangThaiCuonSach;

import java.util.List;

public interface CuonSachService {
    // Queries
    List<CuonSachResponse> getAllCuonSach();
    List<CuonSachResponse> getCuonSachBySach(Long maSach);
    CuonSachResponse getCuonSachById(Long id);
    CuonSach getCuonSachAvailable(Long maSach);
    CuonSach getCuonSachByMaVach(String maVach);

    // Commands
    CuonSachResponse createCuonSach(CuonSachRequest request);
    CuonSachResponse updateCuonSach(Long id, CuonSachRequest request);
    void updateTrangThaiCuonSach(Long maCuonSach, TrangThaiCuonSach trangThai);
    void deleteCuonSach(Long id);
}
