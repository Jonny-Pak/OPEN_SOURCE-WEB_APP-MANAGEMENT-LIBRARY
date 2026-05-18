package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.HinhAnhSachRequest;
import com.hcmunre.library.dto.request.HinhAnhSachUpdateRequest;
import com.hcmunre.library.dto.response.HinhAnhSachResponse;
import com.hcmunre.library.service.HinhAnhSachService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hinh-anh-sach")
public class HinhAnhSachController {

    private final HinhAnhSachService hinhAnhSachService;

    // API lấy toàn bộ danh sách hình ảnh
    @GetMapping
    public ResponseEntity<List<HinhAnhSachResponse>> getAll() {
        return ResponseEntity.ok(hinhAnhSachService.getAllHinhAnh());
    }

    // API lấy danh sách hình ảnh của một đầu sách
    @GetMapping("/sach/{maSach}")
    public ResponseEntity<List<HinhAnhSachResponse>> getBySach(@PathVariable Long maSach) {
        return ResponseEntity.ok(hinhAnhSachService.getHinhAnhBySach(maSach));
    }

    // API lấy chi tiết một hình ảnh qua ID
    @GetMapping("/{id}")
    public ResponseEntity<HinhAnhSachResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(hinhAnhSachService.getHinhAnhById(id));
    }

    // API thêm mới một hình ảnh sách
    @PostMapping
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<HinhAnhSachResponse> create(@Valid @RequestBody HinhAnhSachRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hinhAnhSachService.createHinhAnh(request));
    }

    // API cập nhật loại ảnh và thứ tự hiển thị
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<HinhAnhSachResponse> update(
            @PathVariable Long id,
            @RequestBody HinhAnhSachUpdateRequest request) {
        return ResponseEntity.ok(hinhAnhSachService.updateHinhAnh(id, request));
    }

    // API xóa bỏ một hình ảnh sách khỏi hệ thống
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hinhAnhSachService.deleteHinhAnh(id);
        return ResponseEntity.noContent().build();
    }
}
