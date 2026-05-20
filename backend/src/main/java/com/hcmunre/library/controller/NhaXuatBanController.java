package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.NhaXuatBanRequest;
import com.hcmunre.library.dto.response.NhaXuatBanResponse;
import com.hcmunre.library.service.NhaXuatBanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/nha-xuat-ban")
public class NhaXuatBanController {

    private final NhaXuatBanService nhaXuatBanService;

    // API lấy toàn bộ danh sách nhà xuất bản
    @GetMapping
    public ResponseEntity<List<NhaXuatBanResponse>> getAll() {
        return ResponseEntity.ok(nhaXuatBanService.getAllNhaXuatBan());
    }

    // API tìm kiếm nhà xuất bản theo từ khóa
    @GetMapping("/search")
    public ResponseEntity<List<NhaXuatBanResponse>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(nhaXuatBanService.searchNhaXuatBan(keyword));
    }

    // API lấy chi tiết một nhà xuất bản qua ID
    @GetMapping("/{id}")
    public ResponseEntity<NhaXuatBanResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(nhaXuatBanService.getNhaXuatBanById(id));
    }

    // API tạo mới một nhà xuất bản — @Valid kích hoạt Bean Validation
    @PostMapping
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<NhaXuatBanResponse> create(@Valid @RequestBody NhaXuatBanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nhaXuatBanService.createNhaXuatBan(request));
    }

    // API cập nhật thông tin cho nhà xuất bản hiện có — @Valid kích hoạt Bean Validation
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<NhaXuatBanResponse> update(@PathVariable Long id, @Valid @RequestBody NhaXuatBanRequest request) {
        return ResponseEntity.ok(nhaXuatBanService.updateNhaXuatBan(id, request));
    }

    // API xóa bỏ một nhà xuất bản khỏi hệ thống
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        nhaXuatBanService.deleteNhaXuatBan(id);
        return ResponseEntity.noContent().build();
    }
}
