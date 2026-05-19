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
@RequestMapping({"/api/danh-muc", "/api/v1/danh-muc"})
public class DanhMucController {

    private final TheLoaiService theLoaiService;

    @GetMapping
    public ResponseEntity<List<TheLoaiResponse>> getAll() {
        return ResponseEntity.ok(theLoaiService.getAllTheLoai());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheLoaiResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(theLoaiService.getTheLoaiById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<TheLoaiResponse> create(@Valid @RequestBody TheLoaiRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(theLoaiService.createTheLoai(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<TheLoaiResponse> update(@PathVariable Long id, @Valid @RequestBody TheLoaiRequest request) {
        return ResponseEntity.ok(theLoaiService.updateTheLoai(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('THU_THU', 'QUAN_TRI_VIEN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        theLoaiService.deleteTheLoai(id);
        return ResponseEntity.noContent().build();
    }
}
