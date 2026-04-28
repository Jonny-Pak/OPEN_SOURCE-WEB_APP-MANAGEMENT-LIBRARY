package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.SachRequest;
import com.hcmunre.library.entity.Sach;
import com.hcmunre.library.service.SachService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sach")
public class SachController {

    private final SachService sachService;

    // API lấy toàn bộ danh sách đầu sách
    @GetMapping
    public ResponseEntity<List<Sach>> getAll() {
        return ResponseEntity.ok(sachService.getAllSach());
    }

    // API tìm kiếm đầu sách theo từ khóa
    @GetMapping("/search")
    public ResponseEntity<List<Sach>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(sachService.searchSach(keyword));
    }

    // API lấy chi tiết một đầu sách qua ID
    @GetMapping("/{id}")
    public ResponseEntity<Sach> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sachService.getSachById(id));
    }

    // API tạo mới một đầu sách — @Valid kích hoạt Bean Validation trên SachRequest
    @PostMapping
    public ResponseEntity<Sach> create(@Valid @RequestBody SachRequest request) {
        return ResponseEntity.ok(sachService.createSach(request));
    }

    // API cập nhật thông tin cho đầu sách hiện có — @Valid kích hoạt Bean Validation
    @PutMapping("/{id}")
    public ResponseEntity<Sach> update(@PathVariable Long id, @Valid @RequestBody SachRequest request) {
        return ResponseEntity.ok(sachService.updateSach(id, request));
    }

    // API xóa bỏ một đầu sách khỏi hệ thống
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sachService.deleteSach(id);
        return ResponseEntity.noContent().build();
    }
}
