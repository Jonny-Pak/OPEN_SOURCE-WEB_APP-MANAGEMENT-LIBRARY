package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.AuthRequest;
import com.hcmunre.library.dto.request.RegisterRequest;
import com.hcmunre.library.dto.response.AuthResponse;
import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.enums.VaiTro;
import com.hcmunre.library.exception.BusinessException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.exception.UnauthorizedException;
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

/**
 * Triển khai các chức năng xác thực: đăng nhập và đăng ký.
 * Tương tác trực tiếp với database thông qua NguoiDungRepository.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {

        private final AuthenticationManager authenticationManager;
        private final JwtTokenProvider jwtTokenProvider;
        private final NguoiDungRepository nguoiDungRepository;
        private final PasswordEncoder passwordEncoder;

        /**
         * Xử lý đăng nhập người dùng.
         * Quy trình:
         * 1. Kiểm tra tài khoản có bị khóa không
         * 2. Xác thực email + mật khẩu qua AuthenticationManager
         * 3. Nếu thành công, tạo JWT token và trả về
         *
         * @param request chứa email và mật khẩu
         * @return AuthResponse với JWT token và thông tin người dùng
         * @throws BusinessException     nếu tài khoản bị khóa
         * @throws UnauthorizedException nếu thông tin đăng nhập sai
         */
        @Override
        public AuthResponse dangNhap(AuthRequest request) {
                // Bước 1: Kiểm tra tài khoản có tồn tại và bị khóa không
                NguoiDung nguoiDung = nguoiDungRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new UnauthorizedException(ErrorCode.DANG_NHAP_THAT_BAI));

                if (nguoiDung.getTrangThai() == TrangThaiNguoiDung.KHOA) {
                        throw new BusinessException(ErrorCode.TAI_KHOAN_BI_KHOA);
                }

                try {
                        // Bước 2: Xác thực qua Spring Security AuthenticationManager
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getMatKhau()));

                        // Bước 3: Tạo JWT token
                        String jwtToken = jwtTokenProvider.taoToken(authentication);

                        // Bước 4: Lấy thông tin user từ Authentication
                        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                        NguoiDung nd = userDetails.getNguoiDung();

                        log.info("Đăng nhập thành công cho email: {}", request.getEmail());

                        // Bước 5: Trả về response
                        return AuthResponse.builder()
                                        .accessToken(jwtToken)
                                        .hoDem(nd.getHoDem())
                                        .ten(nd.getTen())
                                        .email(nd.getEmail())
                                        .vaiTro(nd.getVaiTro().name())
                                        .build();

                } catch (BadCredentialsException e) {
                        log.warn("Đăng nhập thất bại cho email: {}", request.getEmail());
                        throw new UnauthorizedException(ErrorCode.DANG_NHAP_THAT_BAI);
                }
        }

        /**
         * Xử lý đăng ký tài khoản mới.
         * Quy trình:
         * 1. Kiểm tra email đã tồn tại chưa
         * 2. Mã hóa mật khẩu bằng BCrypt
         * 3. Tạo entity NguoiDung với vai trò mặc định DOC_GIA
         * 4. Lưu vào database
         * 5. Tự động đăng nhập và trả về JWT token
         *
         * @param request chứa thông tin đăng ký
         * @return AuthResponse với JWT token và thông tin người dùng mới
         * @throws BusinessException nếu email đã tồn tại
         */
        @Override
        @Transactional
        public AuthResponse dangKy(RegisterRequest request) {
                // Bước 1: Kiểm tra email trùng lặp
                if (nguoiDungRepository.existsByEmail(request.getEmail())) {
                        throw new BusinessException(ErrorCode.EMAIL_DA_TON_TAI);
                }
                if (nguoiDungRepository.existsBySoDienThoai(request.getSoDienThoai())) {
                        throw new BusinessException(ErrorCode.SO_DIEN_THOAI_DA_TON_TAI);
                }
                // Bước 2: Tạo entity NguoiDung mới
                NguoiDung nguoiDung = NguoiDung.builder()
                                .hoDem(request.getHoDem())
                                .ten(request.getTen())
                                .email(request.getEmail())
                                .matKhau(passwordEncoder.encode(request.getMatKhau())) // Mã hóa BCrypt
                                .soDienThoai(request.getSoDienThoai())
                                .vaiTro(VaiTro.DOC_GIA) // Vai trò mặc định: Đọc giả
                                .trangThai(TrangThaiNguoiDung.HOAT_DONG) // Trạng thái: Hoạt động
                                .build();

                // Bước 3: Lưu vào database
                nguoiDungRepository.save(nguoiDung);
                log.info("Đăng ký thành công cho email: {}", request.getEmail());

                // Bước 4: Tự động đăng nhập sau khi đăng ký
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getMatKhau()));

                // Bước 5: Tạo JWT token và trả về
                String jwtToken = jwtTokenProvider.taoToken(authentication);

                return AuthResponse.builder()
                                .accessToken(jwtToken)
                                .hoDem(nguoiDung.getHoDem())
                                .ten(nguoiDung.getTen())
                                .email(nguoiDung.getEmail())
                                .vaiTro(nguoiDung.getVaiTro().name())
                                .build();
        }
}
