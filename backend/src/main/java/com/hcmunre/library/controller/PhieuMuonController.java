package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.GiaHanRequest;
import com.hcmunre.library.dto.request.MuonSachRequest;
import com.hcmunre.library.dto.request.TraCuonSachRequest;
import com.hcmunre.library.dto.request.TraToanBoRequest;
import com.hcmunre.library.dto.response.GiaHanResponse;
import com.hcmunre.library.dto.response.PhieuMuonResponse;
import com.hcmunre.library.dto.response.PhieuPhatResponse;
import com.hcmunre.library.service.PhieuMuonService;
import com.hcmunre.library.service.PhieuPhatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/phieu-muon")
public class PhieuMuonController {

    private final PhieuMuonService phieuMuonService;
    private final PhieuPhatService phieuPhatService;

    @PostMapping
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<PhieuMuonResponse> createPhieuMuon(
            @Valid @RequestBody MuonSachRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(phieuMuonService.createPhieuMuon(request));
    }

    @PatchMapping("/{maPhieuMuon}/huy")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<Void> cancelPhieuMuon(@PathVariable UUID maPhieuMuon) {
        phieuMuonService.cancelPhieuMuon(maPhieuMuon);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tra-sach")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<PhieuMuonResponse.ChitietResponse> returnCuonSach(
            @Valid @RequestBody TraCuonSachRequest request) {
        return ResponseEntity.ok(phieuMuonService.returnCuonSach(request));
    }

    @PostMapping("/gia-han")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<GiaHanResponse> createGiaHan(
            @Valid @RequestBody GiaHanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(phieuMuonService.createGiaHan(request));
    }

    @GetMapping("/gia-han/{maChiTietPhieuMuon}")
    public ResponseEntity<List<GiaHanResponse>> getLichSuGiaHanByChiTiet(
            @PathVariable UUID maChiTietPhieuMuon) {
        return ResponseEntity.ok(
                phieuMuonService.getLichSuGiaHanByChiTiet(maChiTietPhieuMuon));
    }

    @PostMapping("/phieu-phat")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<PhieuPhatResponse> createPhieuPhat(
            @Valid @RequestBody com.hcmunre.library.dto.request.TaoPhieuPhatRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(phieuPhatService.createPhieuPhat(request));
    }

    @PatchMapping("/phieu-phat/{maPhieuPhat}/thanh-toan")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<PhieuPhatResponse> payPhieuPhat(
            @PathVariable UUID maPhieuPhat) {
        return ResponseEntity.ok(phieuPhatService.payPhieuPhat(maPhieuPhat));
    }

    @PatchMapping("/phieu-phat/{maPhieuPhat}/huy")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<PhieuPhatResponse> cancelPhieuPhat(
            @PathVariable UUID maPhieuPhat) {
        return ResponseEntity.ok(phieuPhatService.cancelPhieuPhat(maPhieuPhat));
    }

    @GetMapping("/phieu-phat/{maChiTietPhieuMuon}")
    public ResponseEntity<List<PhieuPhatResponse>> getPhieuPhatByChiTiet(
            @PathVariable UUID maChiTietPhieuMuon) {
        return ResponseEntity.ok(
                phieuPhatService.getPhieuPhatByChiTiet(maChiTietPhieuMuon));
    }

    @GetMapping("/phieu-phat")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<List<PhieuPhatResponse>> getAllPhieuPhat() {
        return ResponseEntity.ok(phieuPhatService.getAllPhieuPhat());
    }


    @PostMapping("/mat-sach/{maChiTietPhieuMuon}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<Void> reportMatSach(@PathVariable UUID maChiTietPhieuMuon) {
        phieuMuonService.reportMatSach(maChiTietPhieuMuon);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tra-toan-bo")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<PhieuMuonResponse> returnToanBoPhieuMuon(
            @Valid @RequestBody TraToanBoRequest request) {
        return ResponseEntity.ok(phieuMuonService.returnToanBoPhieuMuon(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<List<PhieuMuonResponse>> getAllPhieuMuon() {
        return ResponseEntity.ok(phieuMuonService.getAllPhieuMuon());
    }

    @GetMapping("/{maPhieuMuon}")
    public ResponseEntity<PhieuMuonResponse> getPhieuMuonById(@PathVariable UUID maPhieuMuon) {
        return ResponseEntity.ok(phieuMuonService.getPhieuMuonById(maPhieuMuon));
    }

    @GetMapping("/nguoi-dung/{maNguoiDung}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN') or #maNguoiDung == authentication.principal.nguoiDung.maNguoiDung")
    public ResponseEntity<List<PhieuMuonResponse>> getPhieuMuonByNguoiDung(@PathVariable UUID maNguoiDung) {
        return ResponseEntity.ok(phieuMuonService.getPhieuMuonByNguoiDung(maNguoiDung));
    }

    @GetMapping("/phieu-phat/nguoi-dung/{maNguoiDung}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN') or #maNguoiDung == authentication.principal.nguoiDung.maNguoiDung")
    public ResponseEntity<List<PhieuPhatResponse>> getPhieuPhatByNguoiDung(@PathVariable UUID maNguoiDung) {
        return ResponseEntity.ok(phieuPhatService.getPhieuPhatByNguoiDung(maNguoiDung));
    }

}

