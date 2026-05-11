package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.SachRequest;
import com.hcmunre.library.dto.response.SachResponse;
import com.hcmunre.library.service.SachService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sach")
public class SachController {

    private final SachService sachService;

    // API lấy toàn bộ danh sách đầu sách
    @GetMapping
    public ResponseEntity<List<SachResponse>> getAll() {
        return ResponseEntity.ok(sachService.getAllSach());
    }

    // API tìm kiếm đầu sách theo từ khóa
    @GetMapping("/search")
    public ResponseEntity<List<SachResponse>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(sachService.searchSach(keyword));
    }

    // API lấy chi tiết một đầu sách qua ID
    @GetMapping("/{id}")
    public ResponseEntity<SachResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sachService.getSachById(id));
    }

    // API tạo mới một đầu sách — @Valid kích hoạt Bean Validation trên SachRequest
    @PostMapping
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<SachResponse> create(@Valid @RequestBody SachRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sachService.createSach(request));
    }

    // API cập nhật thông tin cho đầu sách hiện có — @Valid kích hoạt Bean
    // Validation
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<SachResponse> update(@PathVariable Long id, @Valid @RequestBody SachRequest request) {
        return ResponseEntity.ok(sachService.updateSach(id, request));
    }

    // API xóa bỏ một đầu sách khỏi hệ thống
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sachService.deleteSach(id);
        return ResponseEntity.noContent().build();
    }
}
