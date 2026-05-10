package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.TaoPhieuPhatRequest;
import com.hcmunre.library.dto.response.PhieuPhatResponse;
import java.util.List;
import java.util.UUID;

public interface PhieuPhatService {
    // Queries
    List<PhieuPhatResponse> getPhieuPhatByChiTiet(UUID maChiTietPhieuMuon);

    boolean hasPhieuPhatUnpaid(UUID maNguoiDung);

    List<PhieuPhatResponse> getPhieuPhatByNguoiDung(UUID maNguoiDung);
    // Commands
    PhieuPhatResponse createPhieuPhat(UUID maChiTietPhieuMuon, Double tienPhat, String lyDoPhat);
    PhieuPhatResponse createPhieuPhat(TaoPhieuPhatRequest request);

    PhieuPhatResponse cancelPhieuPhat(UUID maPhieuPhat);

    PhieuPhatResponse payPhieuPhat(UUID maPhieuPhat);
}
