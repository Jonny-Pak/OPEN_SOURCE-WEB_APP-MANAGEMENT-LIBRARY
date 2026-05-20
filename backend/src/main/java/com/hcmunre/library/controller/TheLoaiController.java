package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.TheLoaiRequest;
import com.hcmunre.library.dto.response.TheLoaiResponse;
import com.hcmunre.library.service.TheLoaiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/the-loai")
public class TheLoaiController {

    private final TheLoaiService theLoaiService;

    // API lấy toàn bộ danh sách thể loại
    @GetMapping
    public ResponseEntity<List<TheLoaiResponse>> getAll() {
        return ResponseEntity.ok(theLoaiService.getAllTheLoai());
    }

    // API tìm kiếm thể loại theo từ khóa
    @GetMapping("/search")
    public ResponseEntity<List<TheLoaiResponse>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(theLoaiService.searchTheLoai(keyword));
    }

    // API lấy chi tiết một thể loại qua ID
    @GetMapping("/{id}")
    public ResponseEntity<TheLoaiResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(theLoaiService.getTheLoaiById(id));
    }

    // API tạo mới một thể loại sách
    @PostMapping
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<TheLoaiResponse> create(@Valid @RequestBody TheLoaiRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(theLoaiService.createTheLoai(request));
    }

    // API cập nhật thông tin cho thể loại sách hiện có
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<TheLoaiResponse> update(@PathVariable Long id, @Valid @RequestBody TheLoaiRequest request) {
        return ResponseEntity.ok(theLoaiService.updateTheLoai(id, request));
    }

    // API xóa bỏ một thể loại sách khỏi hệ thống
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        theLoaiService.deleteTheLoai(id);
        return ResponseEntity.noContent().build();
    }
}
