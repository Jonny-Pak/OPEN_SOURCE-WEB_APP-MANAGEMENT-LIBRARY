package com.hcmunre.library.controller;

import com.hcmunre.library.dto.response.ThongKeResponse;
import com.hcmunre.library.repository.SachRepository;
import com.hcmunre.library.repository.CuonSachRepository;
import com.hcmunre.library.repository.NguoiDungRepository;
import com.hcmunre.library.repository.PhieuMuonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/thong-ke")
@RequiredArgsConstructor
public class ThongKeController {

    private final SachRepository sachRepository;
    private final CuonSachRepository cuonSachRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final PhieuMuonRepository phieuMuonRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('QUAN_TRI_VIEN', 'THU_THU')")
    public ResponseEntity<ThongKeResponse> getThongKeChung() {
        ThongKeResponse response = ThongKeResponse.builder()
                .tongSoSach(sachRepository.count())
                .tongSoCuonSach(cuonSachRepository.count())
                .tongSoNguoiDung(nguoiDungRepository.count())
                .tongSoPhieuMuon(phieuMuonRepository.count())
                .build();
        return ResponseEntity.ok(response);
    }
}
