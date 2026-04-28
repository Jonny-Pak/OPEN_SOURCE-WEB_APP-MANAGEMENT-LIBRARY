package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.CuonSachRequest;
import com.hcmunre.library.entity.CuonSach;
import com.hcmunre.library.service.CuonSachService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cuon-sach")
public class CuonSachController {

    private final CuonSachService cuonSachService;

    // API lấy toàn bộ danh sách bản sao sách
    @GetMapping
    public ResponseEntity<List<CuonSach>> getAll() {
        return ResponseEntity.ok(cuonSachService.getAllCuonSach());
    }

    // API lấy danh sách bản sao thuộc một đầu sách cụ thể
    @GetMapping("/sach/{maSach}")
    public ResponseEntity<List<CuonSach>> getBySach(@PathVariable Long maSach) {
        return ResponseEntity.ok(cuonSachService.getCuonSachBySach(maSach));
    }

    // API lấy chi tiết một bản sao sách qua ID
    @GetMapping("/{id}")
    public ResponseEntity<CuonSach> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cuonSachService.getCuonSachById(id));
    }

    // API tạo mới một bản sao sách — @Valid kích hoạt Bean Validation
    @PostMapping
    public ResponseEntity<CuonSach> create(@Valid @RequestBody CuonSachRequest request) {
        return ResponseEntity.ok(cuonSachService.createCuonSach(request));
    }

    // API cập nhật thông tin bản sao sách — @Valid kích hoạt Bean Validation
    @PutMapping("/{id}")
    public ResponseEntity<CuonSach> update(@PathVariable Long id, @Valid @RequestBody CuonSachRequest request) {
        return ResponseEntity.ok(cuonSachService.updateCuonSach(id, request));
    }

    // API xóa bỏ một bản sao sách khỏi hệ thống
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cuonSachService.deleteCuonSach(id);
        return ResponseEntity.noContent().build();
    }
}
