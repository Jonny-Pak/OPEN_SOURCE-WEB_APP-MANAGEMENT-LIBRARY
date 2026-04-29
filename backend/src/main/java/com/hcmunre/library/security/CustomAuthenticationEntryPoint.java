package com.hcmunre.library.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hcmunre.library.dto.response.ErrorResponse;
import com.hcmunre.library.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Xử lý lỗi 401 - Unauthorized.
 * Được kích hoạt khi người dùng cố truy cập tài nguyên được bảo vệ
 * mà chưa đăng nhập hoặc token JWT không hợp lệ/hết hạn.
 * Trả về response JSON chuẩn với mã lỗi CHUA_DANG_NHAP.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Xử lý khi có lỗi xác thực (người dùng chưa đăng nhập).
     * Trả về HTTP 401 với body JSON chứa thông tin lỗi chi tiết.
     *
     * @param request       HTTP request gây ra lỗi
     * @param response      HTTP response để ghi thông tin lỗi
     * @param authException ngoại lệ xác thực
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.CHUA_DANG_NHAP;

        // Xây dựng response lỗi chuẩn
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getStatus())
                .error(errorCode.name())
                .message(errorCode.getMessage())
                .path(request.getRequestURI())
                .build();

        // Thiết lập HTTP response
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Ghi response JSON
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
