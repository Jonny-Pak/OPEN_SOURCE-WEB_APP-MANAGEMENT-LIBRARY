package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.*;
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
import com.hcmunre.library.service.EmailOutboxService;
import com.hcmunre.library.service.OtpService;
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
        private final OtpService otpService;
        private final EmailOutboxService emailOutboxService;

        @Override
        public AuthResponse dangNhap(AuthRequest request) {
                NguoiDung nguoiDung = nguoiDungRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new LibraryException(ErrorCode.DANG_NHAP_THAT_BAI));

                if (nguoiDung.getNgayXoa() != null) {
                        throw new LibraryException(ErrorCode.DANG_NHAP_THAT_BAI);
                }

                if (nguoiDung.getTrangThai() == TrangThaiNguoiDung.KHOA) {
                        throw new LibraryException(ErrorCode.TAI_KHOAN_BI_KHOA);
                }

                if (nguoiDung.getTrangThai() == TrangThaiNguoiDung.CHUA_KICH_HOAT) {
                        throw new LibraryException(ErrorCode.TAI_KHOAN_CHUA_KICH_HOAT);
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
                                        .avatar(nd.getAvatar())
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
                                .avatar(nguoiDung.getAvatar())
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
                                .avatar(nguoiDung.getAvatar())
                                .build();
        }

        @Override
        @Transactional
        public void quenMatKhau(ForgotPasswordRequest request) {
                NguoiDung nguoiDung = nguoiDungRepository.findByEmail(request.getEmail()
                        ).orElseThrow(() -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI));

                if(nguoiDung.getTrangThai() == TrangThaiNguoiDung.KHOA){
                        throw new LibraryException(ErrorCode.TAI_KHOAN_BI_KHOA);
                }

                String otp = otpService.taoOtp(request.getEmail());
                String noiDungEmail = "<div style='font-family:Arial;max-width:600px;margin:0 auto'>" +
                        "<h2 style='color:#2563eb'>Đặt Lại Mật Khẩu</h2>" +
                        "<p>Chào <b>" + nguoiDung.getHoTen() + "</b>,</p>" +
                        "<p>Mã OTP của bạn:</p>" +
                        "<div style='background:#f1f5f9;padding:20px;text-align:center;" +
                        "border-radius:8px;margin:20px 0'>" +
                        "<span style='font-size:32px;font-weight:bold;letter-spacing:8px;" +
                        "color:#1e40af'>" + otp + "</span></div>" +
                        "<p>⏰ Mã có hiệu lực trong <b>15 phút</b>.</p>" +
                        "<p style='color:#6b7280;font-size:14px'>Nếu bạn không yêu cầu, " +
                        "vui lòng bỏ qua email này.</p>" +
                        "<p>Trân trọng,<br>Hệ thống Thư viện</p></div>";

                emailOutboxService.lenLichGuiEmail(request.getEmail(),
                        "[Thư viện] Mã OTP đặt lại mật khẩu",
                        noiDungEmail);

                log.info("Đã gửi OTP đă lại mật khẩu cho email {}", request.getEmail());
        }

        @Override
        @Transactional
        public void datLaiMatKhau(ResetPasswordRequest request) {
                NguoiDung nguoiDung = nguoiDungRepository.findByEmail(
                        request.getEmail()).orElseThrow(
                        () -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI));

                if(otpService.daHetHan(request.getEmail())){
                        throw new LibraryException(ErrorCode.OTP_DA_HET_HAN);
                }

                if(!otpService.xacMinhOtp(request.getEmail(), request.getToken())){
                        throw new LibraryException(ErrorCode.OTP_KHONG_HOP_LE);
                }

                if(!request.getMatKhauMoi().equals(request.getXacNhanMatKhau())){
                        throw new LibraryException(ErrorCode.MAT_KHAU_KHONG_KHOP);
                }

                nguoiDung.setMatKhau(passwordEncoder.encode(request.getMatKhauMoi()));
                nguoiDungRepository.save(nguoiDung);

                log.info("Đặt lại mật khẩu thành công cho email: {}", request.getEmail());
        }
}
