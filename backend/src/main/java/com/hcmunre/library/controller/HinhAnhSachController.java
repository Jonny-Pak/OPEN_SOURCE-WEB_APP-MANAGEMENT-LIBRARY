package com.hcmunre.library.controller;

import com.hcmunre.library.dto.response.ApiResponse;
import com.hcmunre.library.entity.HinhAnhSach;
import com.hcmunre.library.service.HinhAnhSachService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hinh-anh-sach")
public class HinhAnhSachController {

    private final HinhAnhSachService hinhAnhSachService;

    // API lấy toàn bộ danh sách hình ảnh
    @GetMapping
    public ResponseEntity<List<HinhAnhSach>> getAll() {
        return ResponseEntity.ok(hinhAnhSachService.getAllHinhAnh());
    }

    // API lấy danh sách hình ảnh của một đầu sách
    @GetMapping("/sach/{maSach}")
    public ResponseEntity<List<HinhAnhSach>> getBySach(@PathVariable Long maSach) {
        return ResponseEntity.ok(hinhAnhSachService.getHinhAnhBySach(maSach));
    }

    // API lấy chi tiết một hình ảnh qua ID
    @GetMapping("/{id}")
    public ResponseEntity<HinhAnhSach> getById(@PathVariable Long id) {
        return ResponseEntity.ok(hinhAnhSachService.getHinhAnhById(id));
    }

    // API thêm mới một hình ảnh sách
    @PostMapping
    public ResponseEntity<HinhAnhSach> create(@RequestBody HinhAnhSach hinhAnhSach) {
        return ResponseEntity.ok(hinhAnhSachService.createHinhAnh(hinhAnhSach));
    }

    // API xóa bỏ một hình ảnh sách khỏi hệ thống
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        hinhAnhSachService.deleteHinhAnh(id);
        return ResponseEntity.ok(new ApiResponse("Xóa thành công hình ảnh có ID: " + id));
    }
}
