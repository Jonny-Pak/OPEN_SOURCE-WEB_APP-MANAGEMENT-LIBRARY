package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.AuthRequest;
import com.hcmunre.library.dto.request.RegisterRequest;
import com.hcmunre.library.dto.request.RefreshTokenRequest;
import com.hcmunre.library.dto.response.AuthResponse;
import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.enums.VaiTro;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.NguoiDungRepository;
import com.hcmunre.library.security.CustomUserDetails;
import com.hcmunre.library.security.JwtTokenProvider;
import com.hcmunre.library.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {

        private final AuthenticationManager authenticationManager;
        private final JwtTokenProvider jwtTokenProvider;
        private final NguoiDungRepository nguoiDungRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public AuthResponse dangNhap(AuthRequest request) {
                NguoiDung nguoiDung = nguoiDungRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new LibraryException(ErrorCode.DANG_NHAP_THAT_BAI));

                if (nguoiDung.getTrangThai() == TrangThaiNguoiDung.KHOA) {
                        throw new LibraryException(ErrorCode.TAI_KHOAN_BI_KHOA);
                }

                try {
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getMatKhau()));

                        String jwtToken = jwtTokenProvider.taoToken(authentication);
                        String refreshToken = jwtTokenProvider.taoRefreshToken(authentication);

                        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                        NguoiDung nd = userDetails.getNguoiDung();

                        log.info("Đăng nhập thành công cho email: {}", request.getEmail());

                        return AuthResponse.builder()
                                        .maNguoiDung(nd.getMaNguoiDung())
                                        .accessToken(jwtToken)
                                        .refreshToken(refreshToken)
                                        .hoDem(nd.getHoDem())
                                        .ten(nd.getTen())
                                        .email(nd.getEmail())
                                        .vaiTro(nd.getVaiTro().name())
                                        .build();

                } catch (BadCredentialsException e) {
                        log.warn("Đăng nhập thất bại cho email: {}", request.getEmail());
                        throw new LibraryException(ErrorCode.DANG_NHAP_THAT_BAI);
                }
        }

        @Override
        @Transactional
        public AuthResponse dangKy(RegisterRequest request) {
                if (nguoiDungRepository.existsByEmail(request.getEmail())) {
                        throw new LibraryException(ErrorCode.EMAIL_DA_TON_TAI);
                }
                if (nguoiDungRepository.existsBySoDienThoai(request.getSoDienThoai())) {
                        throw new LibraryException(ErrorCode.SDT_DA_TON_TAI);
                }
                NguoiDung nguoiDung = NguoiDung.builder()
                                .hoDem(request.getHoDem())
                                .ten(request.getTen())
                                .email(request.getEmail())
                                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                                .soDienThoai(request.getSoDienThoai())
                                .vaiTro(VaiTro.DOC_GIA)
                                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                                .build();

                nguoiDungRepository.save(nguoiDung);
                log.info("Đăng ký thành công cho email: {}", request.getEmail());

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getMatKhau()));

                String jwtToken = jwtTokenProvider.taoToken(authentication);
                String refreshToken = jwtTokenProvider.taoRefreshToken(authentication);

                return AuthResponse.builder()
                                .maNguoiDung(nguoiDung.getMaNguoiDung())
                                .accessToken(jwtToken)
                                .refreshToken(refreshToken)
                                .hoDem(nguoiDung.getHoDem())
                                .ten(nguoiDung.getTen())
                                .email(nguoiDung.getEmail())
                                .vaiTro(nguoiDung.getVaiTro().name())
                                .build();
        }

        @Override
        public AuthResponse refreshToken(RefreshTokenRequest request) {
                String token = request.getRefreshToken();
                if (!jwtTokenProvider.xacThucToken(token)) {
                        throw new LibraryException(ErrorCode.DANG_NHAP_THAT_BAI); // Using existing ErrorCode
                }

                String email = jwtTokenProvider.layEmailTuToken(token);
                NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                                .orElseThrow(() -> new LibraryException(ErrorCode.DANG_NHAP_THAT_BAI));

                if (nguoiDung.getTrangThai() == TrangThaiNguoiDung.KHOA) {
                        throw new LibraryException(ErrorCode.TAI_KHOAN_BI_KHOA);
                }

                CustomUserDetails userDetails = new CustomUserDetails(nguoiDung);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                String jwtToken = jwtTokenProvider.taoToken(authentication);
                String refreshToken = jwtTokenProvider.taoRefreshToken(authentication);

                return AuthResponse.builder()
                                .maNguoiDung(nguoiDung.getMaNguoiDung())
                                .accessToken(jwtToken)
                                .refreshToken(refreshToken)
                                .hoDem(nguoiDung.getHoDem())
                                .ten(nguoiDung.getTen())
                                .email(nguoiDung.getEmail())
                                .vaiTro(nguoiDung.getVaiTro().name())
                                .build();
        }
}
