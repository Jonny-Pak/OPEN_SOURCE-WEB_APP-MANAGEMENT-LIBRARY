package com.hcmunre.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO phản hồi sau khi đăng nhập thành công.
 * Chứa JWT token và thông tin cơ bản của người dùng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** JWT access token */
    private String accessToken;

    /** Loại token (luôn là "Bearer") */
    @Builder.Default
    private String tokenType = "Bearer";

    /** Họ đệm người dùng */
    private String hoDem;

    /** Tên người dùng */
    private String ten;

    /** Email người dùng */
    private String email;

    /** Vai trò (QUAN_TRI_VIEN, THU_THU, DOC_GIA) */
    private String vaiTro;
}
