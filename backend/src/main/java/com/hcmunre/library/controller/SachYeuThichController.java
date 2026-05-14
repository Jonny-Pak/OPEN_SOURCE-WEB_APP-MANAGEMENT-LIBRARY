package com.hcmunre.library.controller;

import com.hcmunre.library.dto.response.SachYeuThichResponse;
import com.hcmunre.library.service.SachYeuThichService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sach-yeu-thich")
public class SachYeuThichController {

    private final SachYeuThichService sachYeuThichService;

    @GetMapping("/nguoi-dung/{maNguoiDung}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN') or #maNguoiDung == authentication.principal.nguoiDung.maNguoiDung")
    public ResponseEntity<List<SachYeuThichResponse>> getDanhSachYeuThich(@PathVariable UUID maNguoiDung) {
        return ResponseEntity.ok(sachYeuThichService.getDanhSachYeuThich(maNguoiDung));
    }

    @PostMapping("/nguoi-dung/{maNguoiDung}/sach/{maSach}")
    @PreAuthorize("#maNguoiDung == authentication.principal.nguoiDung.maNguoiDung")
    public ResponseEntity<Void> themVaoDanhSachYeuThich(@PathVariable UUID maNguoiDung, @PathVariable Long maSach) {
        sachYeuThichService.themVaoDanhSachYeuThich(maNguoiDung, maSach);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/nguoi-dung/{maNguoiDung}/sach/{maSach}")
    @PreAuthorize("#maNguoiDung == authentication.principal.nguoiDung.maNguoiDung")
    public ResponseEntity<Void> xoaKhoiDanhSachYeuThich(@PathVariable UUID maNguoiDung, @PathVariable Long maSach) {
        sachYeuThichService.xoaKhoiDanhSachYeuThich(maNguoiDung, maSach);
        return ResponseEntity.noContent().build();
    }
}