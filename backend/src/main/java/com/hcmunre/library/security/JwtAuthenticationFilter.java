package com.hcmunre.library.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Bộ lọc JWT chạy MỘT LẦN cho mỗi request HTTP.
 * Kiểm tra header "Authorization" để trích xuất và xác thực JWT token.
 * Nếu token hợp lệ, thiết lập Authentication vào SecurityContext
 * để các filter/controller phía sau có thể sử dụng.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Prefix chuẩn trong header Authorization cho Bearer Token.
     */
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Xử lý lọc JWT cho mỗi request.
     * Quy trình:
     * 1. Trích xuất token từ header Authorization
     * 2. Xác thực token
     * 3. Lấy thông tin người dùng từ database
     * 4. Thiết lập Authentication vào SecurityContext
     *
     * @param request     HTTP request
     * @param response    HTTP response
     * @param filterChain chuỗi filter tiếp theo
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Bước 1: Trích xuất JWT token từ header
            String jwt = trichXuatTokenTuRequest(request);

            // Bước 2: Kiểm tra token có tồn tại và hợp lệ không
            if (StringUtils.hasText(jwt) && jwtTokenProvider.xacThucToken(jwt)) {
                // Bước 3: Lấy email từ token
                String email = jwtTokenProvider.layEmailTuToken(jwt);

                // Bước 4: Tải thông tin người dùng từ database
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // Bước 5: Tạo đối tượng Authentication
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Bước 6: Đặt Authentication vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Không thể thiết lập xác thực người dùng: {}", e.getMessage());
        }

        // Tiếp tục chuỗi filter
        filterChain.doFilter(request, response);
    }

    /**
     * Trích xuất JWT token từ header "Authorization" của request.
     * Header phải có định dạng: "Bearer <token>"
     *
     * @param request HTTP request
     * @return chuỗi JWT token hoặc null nếu không tìm thấy
     */
    private String trichXuatTokenTuRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
