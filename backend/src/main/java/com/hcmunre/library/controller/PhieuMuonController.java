package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.GiaHanRequest;
import com.hcmunre.library.dto.request.MuonSachRequest;
import com.hcmunre.library.dto.request.TraCuonSachRequest;
import com.hcmunre.library.dto.request.TraToanBoRequest;
import com.hcmunre.library.dto.response.GiaHanResponse;
import com.hcmunre.library.dto.response.PhieuMuonResponse;
import com.hcmunre.library.dto.response.PhieuPhatResponse;
import com.hcmunre.library.security.CustomUserDetails;
import com.hcmunre.library.service.PhieuMuonService;
import com.hcmunre.library.service.PhieuPhatService;
import com.hcmunre.library.enums.TrangThaiGiaHan;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/phieu-muon")
public class PhieuMuonController {

    private final PhieuMuonService phieuMuonService;
    private final PhieuPhatService phieuPhatService;
    private final com.hcmunre.library.scheduler.PhieuMuonScheduler phieuMuonScheduler;



    @PostMapping
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN') or (#request.maNguoiDung == authentication.principal.nguoiDung.maNguoiDung)")
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

    @PostMapping("/gia-han/doc-gia/{maChiTietPhieuMuon}")
    @PreAuthorize("hasRole('DOC_GIA')")
    public ResponseEntity<GiaHanResponse> giaHanDocGia(
            @PathVariable UUID maChiTietPhieuMuon,
            @RequestParam(defaultValue = "7") int soNgayGiaHan,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(phieuMuonService.yeuCauGiaHanDocGia(maChiTietPhieuMuon, userDetails.getNguoiDung().getMaNguoiDung(), soNgayGiaHan));
    }

    @GetMapping("/gia-han/danh-sach")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<List<GiaHanResponse>> getDanhSachYeuCauGiaHan(
            @RequestParam(required = false) TrangThaiGiaHan trangThai) {
        return ResponseEntity.ok(phieuMuonService.getDanhSachYeuCauGiaHan(trangThai));
    }

    @PostMapping("/gia-han/duyet/{maLichSuGiaHan}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<GiaHanResponse> duyetYeuCauGiaHan(
            @PathVariable UUID maLichSuGiaHan,
            @RequestParam boolean dongY,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(phieuMuonService.duyetYeuCauGiaHan(maLichSuGiaHan, userDetails.getNguoiDung().getMaNguoiDung(), dongY));
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
    public ResponseEntity<Page<PhieuMuonResponse>> getAllPhieuMuon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayMuon") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(phieuMuonService.getAllPhieuMuon(pageable));
    }

    @GetMapping("/{maPhieuMuon}")
    public ResponseEntity<PhieuMuonResponse> getPhieuMuonById(@PathVariable UUID maPhieuMuon) {
        return ResponseEntity.ok(phieuMuonService.getPhieuMuonById(maPhieuMuon));
    }

    @GetMapping("/cua-toi")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PhieuMuonResponse>> getCuaToi(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "ngayMuon") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(phieuMuonService.getPhieuMuonByNguoiDung(
                userDetails.getNguoiDung().getMaNguoiDung(), pageable));
    }

    @GetMapping("/nguoi-dung/{maNguoiDung}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN') or #maNguoiDung == authentication.principal.nguoiDung.maNguoiDung")
    public ResponseEntity<Page<PhieuMuonResponse>> getPhieuMuonByNguoiDung(
            @PathVariable UUID maNguoiDung,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayMuon") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(phieuMuonService.getPhieuMuonByNguoiDung(maNguoiDung, pageable));
    }

    @GetMapping("/phieu-phat/nguoi-dung/{maNguoiDung}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN') or #maNguoiDung == authentication.principal.nguoiDung.maNguoiDung")
    public ResponseEntity<List<PhieuPhatResponse>> getPhieuPhatByNguoiDung(@PathVariable UUID maNguoiDung) {
        return ResponseEntity.ok(phieuPhatService.getPhieuPhatByNguoiDung(maNguoiDung));
    }

    @GetMapping("/phieu-phat")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<Page<PhieuPhatResponse>> getAllPhieuPhat(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayTao") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(phieuPhatService.getAllPhieuPhat(pageable));
    }

}
