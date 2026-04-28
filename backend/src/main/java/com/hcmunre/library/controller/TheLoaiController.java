package com.hcmunre.library.controller;

import com.hcmunre.library.entity.TheLoai;
import com.hcmunre.library.service.TheLoaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/the-loai")
public class TheLoaiController {

    private final TheLoaiService theLoaiService;

    // API lấy toàn bộ danh sách thể loại
    @GetMapping
    public ResponseEntity<List<TheLoai>> getAll() {
        return ResponseEntity.ok(theLoaiService.getAllTheLoai());
    }

    // API tìm kiếm thể loại theo từ khóa
    @GetMapping("/search")
    public ResponseEntity<List<TheLoai>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(theLoaiService.searchTheLoai(keyword));
    }

    // API lấy chi tiết một thể loại qua ID
    @GetMapping("/{id}")
    public ResponseEntity<TheLoai> getById(@PathVariable Long id) {
        return ResponseEntity.ok(theLoaiService.getTheLoaiById(id));
    }

    // API tạo mới một thể loại sách
    @PostMapping
    public ResponseEntity<TheLoai> create(@RequestBody TheLoai theLoai) {
        return ResponseEntity.ok(theLoaiService.createTheLoai(theLoai));
    }

    // API cập nhật thông tin cho thể loại sách hiện có
    @PutMapping("/{id}")
    public ResponseEntity<TheLoai> update(@PathVariable Long id, @RequestBody TheLoai theLoai) {
        return ResponseEntity.ok(theLoaiService.updateTheLoai(id, theLoai));
    }

    // API xóa bỏ một thể loại sách khỏi hệ thống
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        theLoaiService.deleteTheLoai(id);
        return ResponseEntity.noContent().build();
    }
}
