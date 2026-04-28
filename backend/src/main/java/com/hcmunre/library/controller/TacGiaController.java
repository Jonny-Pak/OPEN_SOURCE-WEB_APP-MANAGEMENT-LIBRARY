package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.TacGiaRequest;
import com.hcmunre.library.entity.TacGia;
import com.hcmunre.library.service.TacGiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tac-gia")
public class TacGiaController {

    private final TacGiaService tacGiaService;

    @GetMapping
    public ResponseEntity<List<TacGia>> getAll() {
        return ResponseEntity.ok(tacGiaService.getAllTacGia());
    }

    @GetMapping("/search")
    public ResponseEntity<List<TacGia>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(tacGiaService.searchTacGia(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TacGia> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tacGiaService.getTacGiaById(id));
    }

    @PostMapping
    public ResponseEntity<TacGia> create(@Valid @RequestBody TacGiaRequest request) {
        return ResponseEntity.ok(tacGiaService.createTacGia(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TacGia> update(@PathVariable Long id, @Valid @RequestBody TacGiaRequest request) {
        return ResponseEntity.ok(tacGiaService.updateTacGia(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tacGiaService.deleteTacGia(id);
        return ResponseEntity.noContent().build();
    }
}
