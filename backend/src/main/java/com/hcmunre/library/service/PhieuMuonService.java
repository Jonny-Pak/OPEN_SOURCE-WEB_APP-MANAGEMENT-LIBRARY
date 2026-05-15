package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.GiaHanRequest;
import com.hcmunre.library.dto.request.MuonSachRequest;
import com.hcmunre.library.dto.request.TraCuonSachRequest;
import com.hcmunre.library.dto.request.TraToanBoRequest;
import com.hcmunre.library.dto.response.GiaHanResponse;
import com.hcmunre.library.dto.response.PhieuMuonResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PhieuMuonService {
    // Queries
    List<GiaHanResponse> getLichSuGiaHanByChiTiet(UUID maChiTietPhieuMuon);

    Page<PhieuMuonResponse> getAllPhieuMuon(Pageable pageable);

    PhieuMuonResponse getPhieuMuonById(UUID maPhieuMuon);

    Page<PhieuMuonResponse> getPhieuMuonByNguoiDung(UUID maNguoiDung, Pageable pageable);

    boolean checkCuonSachQuaHan(UUID maNguoiDung);

    // Commands
    PhieuMuonResponse createPhieuMuon(MuonSachRequest request);

    PhieuMuonResponse.ChitietResponse returnCuonSach(TraCuonSachRequest request);

    void cancelPhieuMuon(UUID maPhieuMuon);

    GiaHanResponse createGiaHan(GiaHanRequest request);

    void reportMatSach(UUID maChiTietPhieuMuon);

    PhieuMuonResponse returnToanBoPhieuMuon(TraToanBoRequest request);
}
