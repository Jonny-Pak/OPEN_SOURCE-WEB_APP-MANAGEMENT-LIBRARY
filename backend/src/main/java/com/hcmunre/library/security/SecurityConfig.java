package com.hcmunre.library.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Cấu hình bảo mật chính của ứng dụng.
 * - Disable CSRF (vì dùng JWT, không cần CSRF token)
 * - Stateless session (không lưu session trên server)
 * - CORS mở hoàn toàn (cho phép Frontend gọi API từ bất kỳ origin nào)
 * - Phân quyền endpoint: /api/auth/** cho phép tất cả, còn lại yêu cầu xác thực
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Bean mã hóa mật khẩu sử dụng thuật toán BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cung cấp AuthenticationProvider sử dụng DAO.
     * Kết hợp CustomUserDetailsService và BCryptPasswordEncoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean AuthenticationManager để sử dụng trong AuthService.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Cấu hình CORS mở hoàn toàn.
     * Cho phép mọi origin, method, header để Frontend gọi không bị block.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Cấu hình chuỗi bộ lọc bảo mật (Security Filter Chain).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Tắt CSRF vì dùng JWT (stateless)
            .csrf(AbstractHttpConfigurer::disable)
            // Cấu hình CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Không tạo session (stateless)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Xử lý lỗi xác thực và phân quyền
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            )
            // Phân quyền các endpoint
            .authorizeHttpRequests(auth -> auth
                // Cho phép gọi xác thực đăng nhập / đăng ký
                .requestMatchers("/api/auth/**").permitAll()
                // Mở khoá cho Swagger Document
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                // Cho phép xem (GET) công khai cho một số API thông tin sách
                .requestMatchers(HttpMethod.GET, "/api/v1/sach/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/tac-gia/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/the-loai/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/nha-xuat-ban/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/hinh-anh-sach/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/cuon-sach/**").permitAll()
                .requestMatchers("/api/v1/lien-he/**").permitAll()
                // Tất cả request khác BẮT BUỘC phải đăng nhập (có token)
                .anyRequest().authenticated()
            )
            // Đăng ký AuthenticationProvider
            .authenticationProvider(authenticationProvider())
            // Thêm JWT filter trước UsernamePasswordAuthenticationFilter
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
