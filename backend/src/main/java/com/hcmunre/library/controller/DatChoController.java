package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.DatChoRequest;
import com.hcmunre.library.dto.response.DatChoResponse;
import com.hcmunre.library.service.DatChoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dat-cho")
public class DatChoController {

    private final DatChoService datChoService;

    @GetMapping
    public ResponseEntity<List<DatChoResponse>> getAll() {
        return ResponseEntity.ok(datChoService.getAllDatCho());
    }

    @GetMapping("/nguoi-dung/{maNguoiDung}")
    public ResponseEntity<List<DatChoResponse>> getByNguoiDung(@PathVariable UUID maNguoiDung) {
        return ResponseEntity.ok(datChoService.getDatChoByNguoiDung(maNguoiDung));
    }

    @PostMapping
    public ResponseEntity<DatChoResponse> create(@Valid @RequestBody DatChoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(datChoService.createDatCho(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable UUID id, @RequestParam(required = false) String ghiChu) {
        datChoService.cancelDatCho(id, ghiChu);
        return ResponseEntity.noContent().build();
    }
}
