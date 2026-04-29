package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.AuthRequest;
import com.hcmunre.library.dto.request.RegisterRequest;
import com.hcmunre.library.dto.response.AuthResponse;
import com.hcmunre.library.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các API xác thực (Authentication).
 * Tất cả endpoint trong controller này đều được permitAll (không cần token).
 * Base URL: /api/auth
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * API đăng nhập.
     * Nhận email + mật khẩu, trả về JWT token nếu thành công.
     *
     * @param request DTO chứa email và mật khẩu
     * @return AuthResponse chứa JWT token và thông tin người dùng
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> dangNhap(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.dangNhap(request);
        return ResponseEntity.ok(response);
    }

    /**
     * API đăng ký tài khoản mới.
     * Nhận thông tin đăng ký, tạo tài khoản với vai trò DOC_GIA,
     * tự động đăng nhập và trả về JWT token.
     *
     * @param request DTO chứa thông tin đăng ký
     * @return AuthResponse chứa JWT token và thông tin người dùng mới
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> dangKy(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.dangKy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
