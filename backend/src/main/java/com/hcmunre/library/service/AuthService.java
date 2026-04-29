package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.AuthRequest;
import com.hcmunre.library.dto.request.RegisterRequest;
import com.hcmunre.library.dto.response.AuthResponse;

/**
 * Interface định nghĩa các chức năng xác thực (Authentication).
 */
public interface AuthService {

    /**
     * Xử lý đăng nhập.
     *
     * @param request chứa email và mật khẩu
     * @return AuthResponse chứa JWT token và thông tin user
     */
    AuthResponse dangNhap(AuthRequest request);

    /**
     * Xử lý đăng ký tài khoản mới.
     *
     * @param request chứa thông tin đăng ký (họ tên, email, mật khẩu, SĐT)
     * @return AuthResponse chứa JWT token và thông tin user đã tạo
     */
    AuthResponse dangKy(RegisterRequest request);
}
