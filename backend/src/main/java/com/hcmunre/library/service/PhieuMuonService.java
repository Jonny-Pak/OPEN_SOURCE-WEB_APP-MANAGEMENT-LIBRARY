package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.GiaHanRequest;
import com.hcmunre.library.dto.request.MuonSachRequest;
import com.hcmunre.library.dto.request.TraSachRequest;
import com.hcmunre.library.dto.response.GiaHanResponse;
import com.hcmunre.library.dto.response.PhieuMuonResponse;
import com.hcmunre.library.enums.TinhTrangSach;

import java.util.List;
import java.util.UUID;

public interface PhieuMuonService {
    // Queries
    List<GiaHanResponse> getLichSuGiaHanByChiTiet(UUID maChiTietPhieuMuon);

    // Commands
    PhieuMuonResponse createPhieuMuon(MuonSachRequest request);
    void returnChiTietPhieuMuon(UUID maChiTietPhieuMuon, TinhTrangSach tinhTrangSach);
    void returnPhieuMuon(UUID maPhieuMuon, TinhTrangSach tinhTrangChung);
    PhieuMuonResponse.ChitietResponse returnCuonSach(TraSachRequest request);
    void cancelPhieuMuon(UUID maPhieuMuon);
    GiaHanResponse createGiaHan(GiaHanRequest request);
}
