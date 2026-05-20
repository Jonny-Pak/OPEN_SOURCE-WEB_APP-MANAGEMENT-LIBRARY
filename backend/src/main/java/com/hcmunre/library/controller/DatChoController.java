package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.DatChoRequest;
import com.hcmunre.library.dto.response.DatChoResponse;
import com.hcmunre.library.security.CustomUserDetails;
import com.hcmunre.library.service.DatChoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/v1/dat-cho", "/api/dat-cho"})
public class DatChoController {

    private final DatChoService datChoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<List<DatChoResponse>> getAll() {
        return ResponseEntity.ok(datChoService.getAllDatCho());
    }

    @GetMapping("/nguoi-dung/{maNguoiDung}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN') or #maNguoiDung == authentication.principal.nguoiDung.maNguoiDung")
    public ResponseEntity<List<DatChoResponse>> getByNguoiDung(@PathVariable UUID maNguoiDung) {
        return ResponseEntity.ok(datChoService.getDatChoByNguoiDung(maNguoiDung));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DOC_GIA', 'THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<DatChoResponse> create(@Valid @RequestBody DatChoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(datChoService.createDatCho(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<Void> cancel(@PathVariable UUID id, @RequestParam(required = false) String ghiChu) {
        datChoService.cancelDatCho(id, ghiChu);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/huy")
    @PreAuthorize("hasAnyRole('DOC_GIA', 'THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<Void> cancelPut(@PathVariable UUID id, @RequestParam(required = false) String ghiChu) {
        datChoService.cancelDatCho(id, ghiChu);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/duyet")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<DatChoResponse> duyetDatCho(@PathVariable UUID id) {
        return ResponseEntity.ok(datChoService.duyetDatCho(id));
    }

    @GetMapping("/cua-toi")
    @PreAuthorize("hasAnyRole('DOC_GIA', 'THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<List<DatChoResponse>> getCuaToi(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(datChoService.getDatChoByNguoiDung(userDetails.getNguoiDung().getMaNguoiDung()));
    }
}
