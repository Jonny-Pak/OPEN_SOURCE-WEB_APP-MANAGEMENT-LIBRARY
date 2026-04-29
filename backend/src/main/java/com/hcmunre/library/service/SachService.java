package com.hcmunre.library.service;

import com.hcmunre.library.entity.CuonSach;
import com.hcmunre.library.enums.TrangThaiCuonSach;

public interface SachService {
    CuonSach getCuonSachAvailable(Long maSach);

    void updateTrangThaiCuonSach(Long maCuonSach, TrangThaiCuonSach trangThai);
}
