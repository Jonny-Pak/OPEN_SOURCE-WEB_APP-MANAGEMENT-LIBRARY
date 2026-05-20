package com.hcmunre.library.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Lớp tiện ích quản lý JWT (JSON Web Token).
 * Cung cấp các chức năng: tạo token, lấy username từ token, xác thực token.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    /**
     * Khóa bí mật dùng để ký và xác thực JWT.
     * Được đọc từ file cấu hình application.yaml (jwt.secret).
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Thời gian hết hạn của token (đơn vị: mili giây).
     * Mặc định 7 ngày (604800000 ms) để tiện cho việc dev/test.
     * Được đọc từ file cấu hình application.yaml (jwt.expiration).
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Thời gian hết hạn của refresh token (mặc định 30 ngày = 2592000000 ms).
     */
    @Value("${jwt.refresh-expiration:2592000000}")
    private long jwtRefreshExpiration;

    /**
     * Tạo JWT token từ thông tin xác thực (Authentication).
     *
     * @param authentication đối tượng Authentication chứa thông tin người dùng đã xác thực
     * @return chuỗi JWT token
     */
    public String taoToken(Authentication authentication) {
        // Lấy thông tin CustomUserDetails từ đối tượng xác thực
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Date ngayHienTai = new Date();
        Date ngayHetHan = new Date(ngayHienTai.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())                  // Đặt subject là email người dùng
                .claim("vaiTro", userDetails.getNguoiDung().getVaiTro().name()) // Thêm vai trò vào claims
                .issuedAt(ngayHienTai)                              // Thời điểm phát hành
                .expiration(ngayHetHan)                             // Thời điểm hết hạn
                .signWith(layKhoaBiMat())                           // Ký bằng khóa bí mật
                .compact();
    }

    /**
     * Tạo Refresh token từ thông tin xác thực.
     */
    public String taoRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Date ngayHienTai = new Date();
        Date ngayHetHan = new Date(ngayHienTai.getTime() + jwtRefreshExpiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(ngayHienTai)
                .expiration(ngayHetHan)
                .signWith(layKhoaBiMat())
                .compact();
    }

    /**
     * Tạo Access token trực tiếp từ Entity NguoiDung.
     */
    public String taoTokenTuNguoiDung(com.hcmunre.library.entity.NguoiDung nguoiDung) {
        Date ngayHienTai = new Date();
        Date ngayHetHan = new Date(ngayHienTai.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(nguoiDung.getEmail())
                .claim("vaiTro", nguoiDung.getVaiTro().name())
                .issuedAt(ngayHienTai)
                .expiration(ngayHetHan)
                .signWith(layKhoaBiMat())
                .compact();
    }

    /**
     * Trích xuất email (username) từ JWT token.
     *
     * @param token chuỗi JWT token
     * @return email người dùng được lưu trong subject của token
     */
    public String layEmailTuToken(String token) {
        return Jwts.parser()
                .verifyWith(layKhoaBiMat())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Xác thực tính hợp lệ của JWT token.
     * Kiểm tra chữ ký, thời hạn, định dạng và nội dung của token.
     *
     * @param token chuỗi JWT token cần xác thực
     * @return true nếu token hợp lệ, false nếu không
     */
    public boolean xacThucToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(layKhoaBiMat())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Token JWT không hợp lệ: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT đã hết hạn: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT không được hỗ trợ: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Chuỗi claims JWT rỗng: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Giải mã khóa bí mật từ chuỗi Base64 thành đối tượng SecretKey.
     *
     * @return đối tượng SecretKey dùng để ký/xác thực JWT
     */
    private SecretKey layKhoaBiMat() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
