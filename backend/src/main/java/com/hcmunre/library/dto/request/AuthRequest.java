package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO yêu cầu đăng nhập.
 * Chứa thông tin email và mật khẩu của người dùng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    /** Email đăng nhập - bắt buộc, phải đúng định dạng email */
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    /** Mật khẩu - bắt buộc */
    @NotBlank(message = "Mật khẩu không được để trống")
    private String matKhau;
}
