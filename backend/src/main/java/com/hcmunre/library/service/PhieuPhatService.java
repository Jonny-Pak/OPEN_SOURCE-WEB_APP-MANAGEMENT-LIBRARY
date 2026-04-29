package com.hcmunre.library.service;

import com.hcmunre.library.dto.response.PhieuPhatResponse;

import java.util.List;
import java.util.UUID;

public interface PhieuPhatService {
    PhieuPhatResponse createPhieuPhat(UUID maChiTietPhieuMuon, Double tienPhat, String lyDoPhat);
    PhieuPhatResponse payPhieuPhat(UUID maPhieuPhat);
    List<PhieuPhatResponse> getPhieuPhatByChiTiet(UUID maChiTietPhieuMuon);
    boolean hasPhieuPhatUnpaid(UUID maNguoiDung);
}
