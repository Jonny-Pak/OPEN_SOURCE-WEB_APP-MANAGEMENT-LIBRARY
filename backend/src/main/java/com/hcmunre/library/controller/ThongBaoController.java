package com.hcmunre.library.controller;

import com.hcmunre.library.dto.response.ThongBaoResponse;
import com.hcmunre.library.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/thong-bao")
public class ThongBaoController {

    private final ThongBaoService thongBaoService;

    @GetMapping("/nguoi-dung/{maNguoiDung}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN') or #maNguoiDung == authentication.principal.nguoiDung.maNguoiDung")
    public ResponseEntity<List<ThongBaoResponse>> layDanhSachThongBao(@PathVariable UUID maNguoiDung) {
        return ResponseEntity.ok(thongBaoService.layDanhSachThongBao(maNguoiDung));
    }

    @GetMapping("/nguoi-dung/{maNguoiDung}/chua-doc/count")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN') or #maNguoiDung == authentication.principal.nguoiDung.maNguoiDung")
    public ResponseEntity<Long> demSoThongBaoChuaDoc(@PathVariable UUID maNguoiDung) {
        return ResponseEntity.ok(thongBaoService.demSoThongBaoChuaDoc(maNguoiDung));
    }

    @PutMapping("/{maThongBao}/da-doc")
    public ResponseEntity<Void> danhDauDaDoc(@PathVariable UUID maThongBao) {
        thongBaoService.danhDauDaDoc(maThongBao);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/nguoi-dung/{maNguoiDung}/da-doc-het")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN') or #maNguoiDung == authentication.principal.nguoiDung.maNguoiDung")
    public ResponseEntity<Void> danhDauTatCaDaDoc(@PathVariable UUID maNguoiDung) {
        thongBaoService.danhDauTatCaDaDoc(maNguoiDung);
        return ResponseEntity.noContent().build();
    }
}