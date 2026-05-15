package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.ChangePasswordRequest;
import com.hcmunre.library.dto.request.UpdateProfileRequest;
import com.hcmunre.library.dto.response.NguoiDungResponse;
import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.security.CustomUserDetails;
import com.hcmunre.library.service.NguoiDungService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/v1/nguoi-dung")
@RequiredArgsConstructor
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    // Xem profile cá nhân (Lấy ID từ Token qua @AuthenticationPrincipal)
    @GetMapping("/me")
    public ResponseEntity<NguoiDungResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(nguoiDungService.getMyProfile(userDetails.getNguoiDung().getMaNguoiDung()));
    }

    // Cập nhật profile cá nhân
    @PutMapping("/me")
    public ResponseEntity<NguoiDungResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(nguoiDungService.updateProfile(userDetails.getNguoiDung().getMaNguoiDung(), request));
    }

    // Đổi mật khẩu
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        nguoiDungService.changePassword(userDetails.getNguoiDung().getMaNguoiDung(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('QUAN_TRI_VIEN', 'THU_THU')")
    public ResponseEntity<Page<NguoiDungResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "maNguoiDung") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(nguoiDungService.getAllNguoiDung(pageable));
    }

    @PatchMapping("/{id}/trang-thai")
    @PreAuthorize("hasRole('QUAN_TRI_VIEN')")
    public ResponseEntity<Void> toggleStatus(
            @PathVariable UUID id,
            @RequestParam TrangThaiNguoiDung trangThai) {
        nguoiDungService.toggleUserStatus(id, trangThai);
        return ResponseEntity.ok().build();
    }
}
