package com.hcmunre.library.controller;

import com.hcmunre.library.entity.NhatKyHoatDong;
import com.hcmunre.library.service.NhatKyHoatDongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/nhat-ky")
@RequiredArgsConstructor
public class NhatKyHoatDongController {
    private final NhatKyHoatDongService nhatKyHoatDongService;

    @GetMapping
    @PreAuthorize("hasRole('QUAN_TRI_VIEN')")
    public ResponseEntity<Page<NhatKyHoatDong>> getLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String vaiTro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NhatKyHoatDong> logs = nhatKyHoatDongService.getLogs(keyword, vaiTro, pageable);
        return ResponseEntity.ok(logs);
    }
}
