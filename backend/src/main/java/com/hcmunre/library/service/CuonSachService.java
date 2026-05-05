package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.CuonSachRequest;
import com.hcmunre.library.entity.CuonSach;
import java.util.List;

public interface CuonSachService {
    // Lấy danh sách tất cả bản sao sách
    List<CuonSach> getAllCuonSach();
    // Lấy danh sách bản sao thuộc về một đầu sách
    List<CuonSach> getCuonSachBySach(Long maSach);
    // Lấy thông tin bản sao theo ID
    CuonSach getCuonSachById(Long id);
    // Tạo mới một bản sao sách
    CuonSach createCuonSach(CuonSachRequest request);
    // Cập nhật thông tin bản sao sách
    CuonSach updateCuonSach(Long id, CuonSachRequest request);
    // Xóa bản sao sách theo ID
    void deleteCuonSach(Long id);
}
