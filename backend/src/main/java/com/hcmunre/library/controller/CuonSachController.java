package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.CuonSachRequest;
import com.hcmunre.library.dto.response.CuonSachResponse;
import com.hcmunre.library.dto.response.ImportExcelResponse;
import com.hcmunre.library.service.CuonSachService;
import com.hcmunre.library.service.ImportExcelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cuon-sach")
public class CuonSachController {

    private final CuonSachService cuonSachService;
    private final ImportExcelService importExcelService;

    @GetMapping
    public ResponseEntity<List<CuonSachResponse>> getAll() {
        return ResponseEntity.ok(cuonSachService.getAllCuonSach());
    }

    @GetMapping("/sach/{maSach}")
    public ResponseEntity<List<CuonSachResponse>> getBySach(@PathVariable Long maSach) {
        return ResponseEntity.ok(cuonSachService.getCuonSachBySach(maSach));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuonSachResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cuonSachService.getCuonSachById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<CuonSachResponse> create(@Valid @RequestBody CuonSachRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuonSachService.createCuonSach(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<CuonSachResponse> update(@PathVariable Long id, @Valid @RequestBody CuonSachRequest request) {
        return ResponseEntity.ok(cuonSachService.updateCuonSach(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cuonSachService.deleteCuonSach(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Import danh sách bản sao sách từ file Excel.
     * Cột: STT | Mã sách (ISBN hoặc ID) | Mã vạch | Vị trí kệ | Tình trạng vật lý | Ghi chú
     */
    @PostMapping("/import-excel")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<ImportExcelResponse> importFromExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(importExcelService.importCuonSach(file));
    }
}
