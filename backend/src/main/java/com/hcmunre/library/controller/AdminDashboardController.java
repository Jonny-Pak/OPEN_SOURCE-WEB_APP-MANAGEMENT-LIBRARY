package com.hcmunre.library.controller;

import com.hcmunre.library.dto.response.ThongKeDashboardResponse;
import com.hcmunre.library.entity.PhieuPhat;
import com.hcmunre.library.enums.TrangThaiChiTietPhieuMuon;
import com.hcmunre.library.enums.TrangThaiCuonSach;
import com.hcmunre.library.repository.ChiTietPhieuMuonRepository;
import com.hcmunre.library.repository.CuonSachRepository;
import com.hcmunre.library.repository.NguoiDungRepository;
import com.hcmunre.library.repository.PhieuPhatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final CuonSachRepository cuonSachRepository;
    private final ChiTietPhieuMuonRepository chiTietPhieuMuonRepository;
    private final PhieuPhatRepository phieuPhatRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @GetMapping("/thong-ke")
    @PreAuthorize("hasAnyRole('QUAN_TRI_VIEN', 'THU_THU')")
    public ResponseEntity<ThongKeDashboardResponse> getDashboardStats() {
        long soBanDangMuon = cuonSachRepository.countByTrangThai(TrangThaiCuonSach.DANG_MUON);
        long soSachQuaHan = chiTietPhieuMuonRepository.countByTrangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon.QUA_HAN);
        
        // Tính tổng tiền phạt
        double tongTienPhat = phieuPhatRepository.findAll().stream()
                .mapToDouble(PhieuPhat::getSoTienPhat)
                .sum();
                
        long tongNguoiDung = nguoiDungRepository.count();

        ThongKeDashboardResponse response = ThongKeDashboardResponse.builder()
                .soBanDangMuon(soBanDangMuon)
                .soSachQuaHan(soSachQuaHan)
                .tongTienPhat(tongTienPhat)
                .tongNguoiDung(tongNguoiDung)
                .build();

        return ResponseEntity.ok(response);
    }
}
