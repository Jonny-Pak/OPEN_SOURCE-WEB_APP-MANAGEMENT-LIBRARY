package com.hcmunre.library.security;

import com.hcmunre.library.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Xử lý lỗi 403 - Forbidden.
 * Kích hoạt khi user đã đăng nhập nhưng không đủ quyền.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.KHONG_CO_QUYEN;

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Viết JSON trực tiếp, không phụ thuộc Jackson
        String json = """
                {
                    "timestamp": "%s",
                    "status": %d,
                    "error": "%s",
                    "message": "%s",
                    "path": "%s"
                }
                """.formatted(
                LocalDateTime.now(),
                errorCode.getHttpStatus().value(),
                errorCode.name(),
                errorCode.getMessage(),
                request.getRequestURI()
        );

        response.getWriter().write(json);
    }
}
