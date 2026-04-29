package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.GiaHanRequest;
import com.hcmunre.library.dto.request.MuonSachRequest;
import com.hcmunre.library.dto.request.TraSachRequest;
import com.hcmunre.library.dto.response.GiaHanResponse;
import com.hcmunre.library.dto.response.PhieuMuonResponse;
import com.hcmunre.library.dto.response.PhieuPhatResponse;
import com.hcmunre.library.service.PhieuMuonService;
import com.hcmunre.library.service.PhieuPhatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PhieuMuonResponse> createPhieuMuon(
            @Valid @RequestBody MuonSachRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(phieuMuonService.createPhieuMuon(request));
    }

    @PatchMapping("/{maPhieuMuon}/huy")
    public ResponseEntity<Void> cancelPhieuMuon(@PathVariable UUID maPhieuMuon) {
        phieuMuonService.cancelPhieuMuon(maPhieuMuon);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tra-sach")
    public ResponseEntity<PhieuMuonResponse.ChitietResponse> returnCuonSach(
            @Valid @RequestBody TraSachRequest request) {
        return ResponseEntity.ok(phieuMuonService.returnCuonSach(request));
    }

    @PostMapping("/gia-han")
    public ResponseEntity<GiaHanResponse> createGiaHan(
            @Valid @RequestBody GiaHanRequest request) {
        return ResponseEntity.ok(phieuMuonService.createGiaHan(request));
    }

    @GetMapping("/gia-han/{maChiTietPhieuMuon}")
    public ResponseEntity<List<GiaHanResponse>> getLichSuGiaHanByChiTiet(
            @PathVariable UUID maChiTietPhieuMuon) {
        return ResponseEntity.ok(
                phieuMuonService.getLichSuGiaHanByChiTiet(maChiTietPhieuMuon));
    }

    @PatchMapping("/phieu-phat/{maPhieuPhat}/thanh-toan")
    public ResponseEntity<PhieuPhatResponse> payPhieuPhat(
            @PathVariable UUID maPhieuPhat) {
        return ResponseEntity.ok(phieuPhatService.payPhieuPhat(maPhieuPhat));
    }

    @GetMapping("/phieu-phat/{maChiTietPhieuMuon}")
    public ResponseEntity<List<PhieuPhatResponse>> getPhieuPhatByChiTiet(
            @PathVariable UUID maChiTietPhieuMuon) {
        return ResponseEntity.ok(
                phieuPhatService.getPhieuPhatByChiTiet(maChiTietPhieuMuon));
    }
}
