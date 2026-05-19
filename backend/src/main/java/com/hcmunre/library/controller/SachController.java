package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.SachRequest;
import com.hcmunre.library.dto.response.SachResponse;
import com.hcmunre.library.service.SachService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sach")
public class SachController {

    private final SachService sachService;

    // API lấy toàn bộ danh sách đầu sách có phân trang
    @GetMapping
    public ResponseEntity<Page<SachResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maSach") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(sachService.searchAndFilterSach(keyword, null, null, null, pageable));
        }
        return ResponseEntity.ok(sachService.getAllSach(pageable));
    }

    // API tìm kiếm và lọc nâng cao
    @GetMapping("/search")
    public ResponseEntity<Page<SachResponse>> searchAndFilter(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long maTheLoai,
            @RequestParam(required = false) Long maTacGia,
            @RequestParam(required = false) Long maNhaXuatBan,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maSach") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(sachService.searchAndFilterSach(keyword, maTheLoai, maTacGia, maNhaXuatBan, pageable));
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
