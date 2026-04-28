package com.hcmunre.library.controller;

import com.hcmunre.library.dto.request.ChangePasswordRequest;
import com.hcmunre.library.dto.request.LoginRequest;
import com.hcmunre.library.dto.request.RefreshTokenRequest;
import com.hcmunre.library.dto.request.RegisterRequest;
import com.hcmunre.library.dto.response.LoginResponse;
import com.hcmunre.library.dto.response.RegisterResponse;
import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.enums.VaiTro;
import com.hcmunre.library.repository.NguoiDungRepository;
import com.hcmunre.library.service.JwtService;
import com.hcmunre.library.service.TokenBlacklistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            TokenBlacklistService tokenBlacklistService,
            NguoiDungRepository nguoiDungRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new LoginResponse(
                "Bearer",
                accessToken,
                jwtService.getAccessTokenExpirationSeconds(),
                refreshToken,
                jwtService.getRefreshTokenExpirationSeconds());
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        // Check if email or soDienThoai already exists
        if (nguoiDungRepository.findByEmailOrSoDienThoai(request.getEmail(), request.getSoDienThoai()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Email hoặc số điện thoại đã được sử dụng");
        }

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setEmail(request.getEmail());
        nguoiDung.setSoDienThoai(request.getSoDienThoai());
        nguoiDung.setHoDem(request.getHoDem());
        nguoiDung.setTen(request.getTen());
        nguoiDung.setMatKhau(passwordEncoder.encode(request.getPassword()));
        nguoiDung.setVaiTro(VaiTro.DOC_GIA);
        nguoiDung.setTrangThai(TrangThaiNguoiDung.HOAT_DONG);

        NguoiDung savedUser = nguoiDungRepository.save(nguoiDung);

        return new RegisterResponse(
                savedUser.getMaNguoiDung(),
                savedUser.getEmail(),
                savedUser.getSoDienThoai(),
                savedUser.getHoDem(),
                savedUser.getTen(),
                "Đăng ký thành công");
    }

    @PostMapping("/change-password")
    public Map<String, String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập");
        }

        String username = authentication.getName();
        NguoiDung nguoiDung = nguoiDungRepository.findByEmailOrSoDienThoai(username, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Người dùng không tồn tại"));

        // Validate old password
        if (!passwordEncoder.matches(request.getOldPassword(), nguoiDung.getMatKhau())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu cũ không đúng");
        }

        // Check if new password is same as old password
        if (passwordEncoder.matches(request.getNewPassword(), nguoiDung.getMatKhau())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mật khẩu mới phải khác với mật khẩu cũ");
        }

        nguoiDung.setMatKhau(passwordEncoder.encode(request.getNewPassword()));
        nguoiDungRepository.save(nguoiDung);

        return Map.of("message", "Thay đổi mật khẩu thành công");
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            if (tokenBlacklistService.isBlacklisted(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token không hợp lệ");
            }

            String username = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Refresh token đã hết hạn hoặc không hợp lệ");
            }

            String newAccessToken = jwtService.generateAccessToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            tokenBlacklistService.blacklist(refreshToken, jwtService.extractExpiration(refreshToken));

            return new LoginResponse(
                    "Bearer",
                    newAccessToken,
                    jwtService.getAccessTokenExpirationSeconds(),
                    newRefreshToken,
                    jwtService.getRefreshTokenExpirationSeconds());
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token không hợp lệ");
        }
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu access token");
        }

        try {
            String accessToken = authorization.substring(7);
            if (!jwtService.isAccessToken(accessToken)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token không hợp lệ");
            }
            tokenBlacklistService.blacklist(accessToken, jwtService.extractExpiration(accessToken));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access token không hợp lệ");
        }

        return Map.of("message", "Đăng xuất thành công");
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }
}
