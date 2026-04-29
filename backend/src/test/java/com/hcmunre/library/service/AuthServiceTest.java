package com.hcmunre.library.service;

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
import com.hcmunre.library.service.implement.AuthServiceImplement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Unit Test cho AuthService.
 * Sử dụng JUnit 5 + Mockito + BDD style (given/when/then).
 * Tất cả test chạy độc lập, không phụ thuộc database.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Kiểm thử chức năng xác thực")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private NguoiDungRepository nguoiDungRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImplement authService;

    // ===== Dữ liệu test dùng chung =====
    private NguoiDung nguoiDungMau;
    private AuthRequest authRequestMau;
    private RegisterRequest registerRequestMau;
    private Authentication authenticationMau;
    private static final String JWT_TOKEN_MAU = "eyJhbGciOiJIUzI1NiJ9.test-token";
    private static final String EMAIL_MAU = "nguyenvana@gmail.com";
    private static final String MAT_KHAU_MAU = "matkhau123";
    private static final String MAT_KHAU_MA_HOA = "$2a$10$encodedPassword";

    /**
     * Khởi tạo dữ liệu test trước mỗi test case.
     */
    @BeforeEach
    void khoiTao() {
        // Tạo entity NguoiDung mẫu
        nguoiDungMau = NguoiDung.builder()
                .maNguoiDung(UUID.randomUUID())
                .hoDem("Nguyễn Văn")
                .ten("A")
                .email(EMAIL_MAU)
                .matKhau(MAT_KHAU_MA_HOA)
                .soDienThoai("0901234567")
                .vaiTro(VaiTro.DOC_GIA)
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build();

        // Tạo request đăng nhập mẫu
        authRequestMau = AuthRequest.builder()
                .email(EMAIL_MAU)
                .matKhau(MAT_KHAU_MAU)
                .build();

        // Tạo request đăng ký mẫu
        registerRequestMau = RegisterRequest.builder()
                .hoDem("Trần Thị")
                .ten("B")
                .email("tranthib@gmail.com")
                .matKhau("matkhau456")
                .soDienThoai("0912345678")
                .build();

        // Tạo Authentication mock
        CustomUserDetails userDetails = new CustomUserDetails(nguoiDungMau);
        authenticationMau = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }

    // ==========================================
    // ĐĂNG NHẬP - TEST CASES
    // ==========================================

    @Test
    @DisplayName("Đăng nhập thành công - Trả về AuthResponse chứa JWT")
    void testLogin_Success() {
        // GIVEN: Email tồn tại trong DB, tài khoản đang hoạt động
        given(nguoiDungRepository.findByEmail(EMAIL_MAU))
                .willReturn(Optional.of(nguoiDungMau));

        // GIVEN: AuthenticationManager xác thực thành công
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authenticationMau);

        // GIVEN: JwtTokenProvider tạo token thành công
        given(jwtTokenProvider.taoToken(authenticationMau))
                .willReturn(JWT_TOKEN_MAU);

        // WHEN: Gọi hàm đăng nhập
        AuthResponse response = authService.dangNhap(authRequestMau);

        // THEN: Kiểm tra response trả về đúng
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(JWT_TOKEN_MAU);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getEmail()).isEqualTo(EMAIL_MAU);
        assertThat(response.getHoDem()).isEqualTo("Nguyễn Văn");
        assertThat(response.getTen()).isEqualTo("A");
        assertThat(response.getVaiTro()).isEqualTo(VaiTro.DOC_GIA.name());

        // THEN: Xác nhận các hàm được gọi đúng
        verify(nguoiDungRepository).findByEmail(EMAIL_MAU);
        verify(authenticationManager).authenticate(any());
        verify(jwtTokenProvider).taoToken(authenticationMau);
    }

    @Test
    @DisplayName("Đăng nhập thất bại - Sai mật khẩu, ném UnauthorizedException")
    void testLogin_WrongPassword() {
        // GIVEN: Email tồn tại trong DB
        given(nguoiDungRepository.findByEmail(EMAIL_MAU))
                .willReturn(Optional.of(nguoiDungMau));

        // GIVEN: AuthenticationManager ném BadCredentialsException (sai mật khẩu)
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("Bad credentials"));

        // WHEN & THEN: Gọi đăng nhập -> ném UnauthorizedException
        assertThatThrownBy(() -> authService.dangNhap(authRequestMau))
                .isInstanceOf(UnauthorizedException.class)
                .satisfies(ex -> {
                    UnauthorizedException ue = (UnauthorizedException) ex;
                    assertThat(ue.getErrorCode()).isEqualTo(ErrorCode.DANG_NHAP_THAT_BAI);
                });

        // THEN: JwtTokenProvider không được gọi (vì xác thực thất bại)
        verify(jwtTokenProvider, never()).taoToken(any());
    }

    @Test
    @DisplayName("Đăng nhập thất bại - Tài khoản bị khóa, ném BusinessException")
    void testLogin_AccountLocked() {
        // GIVEN: Tài khoản bị khóa
        nguoiDungMau.setTrangThai(TrangThaiNguoiDung.KHOA);
        given(nguoiDungRepository.findByEmail(EMAIL_MAU))
                .willReturn(Optional.of(nguoiDungMau));

        // WHEN & THEN: Gọi đăng nhập -> ném BusinessException (TAI_KHOAN_BI_KHOA)
        assertThatThrownBy(() -> authService.dangNhap(authRequestMau))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.TAI_KHOAN_BI_KHOA);
                });

        // THEN: AuthenticationManager không được gọi
        verify(authenticationManager, never()).authenticate(any());
    }

    // ==========================================
    // ĐĂNG KÝ - TEST CASES
    // ==========================================

    @Test
    @DisplayName("Đăng ký thành công - Lưu DB, mã hóa mật khẩu, trả về JWT")
    void testRegister_Success() {
        // GIVEN: Email chưa tồn tại
        given(nguoiDungRepository.existsByEmail(registerRequestMau.getEmail()))
                .willReturn(false);

        // GIVEN: PasswordEncoder mã hóa mật khẩu
        given(passwordEncoder.encode(registerRequestMau.getMatKhau()))
                .willReturn(MAT_KHAU_MA_HOA);

        // GIVEN: Repository lưu thành công
        given(nguoiDungRepository.save(any(NguoiDung.class)))
                .willAnswer(invocation -> {
                    NguoiDung nd = invocation.getArgument(0);
                    nd.setMaNguoiDung(UUID.randomUUID());
                    return nd;
                });

        // GIVEN: Tạo mock cho đăng nhập tự động sau đăng ký
        NguoiDung nguoiDungMoi = NguoiDung.builder()
                .maNguoiDung(UUID.randomUUID())
                .hoDem(registerRequestMau.getHoDem())
                .ten(registerRequestMau.getTen())
                .email(registerRequestMau.getEmail())
                .matKhau(MAT_KHAU_MA_HOA)
                .vaiTro(VaiTro.DOC_GIA)
                .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                .build();
        CustomUserDetails newUserDetails = new CustomUserDetails(nguoiDungMoi);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newUserDetails, null, newUserDetails.getAuthorities()
        );

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(newAuth);
        given(jwtTokenProvider.taoToken(any(Authentication.class)))
                .willReturn(JWT_TOKEN_MAU);

        // WHEN: Gọi hàm đăng ký
        AuthResponse response = authService.dangKy(registerRequestMau);

        // THEN: Kiểm tra response
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(JWT_TOKEN_MAU);
        assertThat(response.getEmail()).isEqualTo(registerRequestMau.getEmail());
        assertThat(response.getVaiTro()).isEqualTo(VaiTro.DOC_GIA.name());

        // THEN: Xác nhận mật khẩu được mã hóa BCrypt
        verify(passwordEncoder).encode(registerRequestMau.getMatKhau());

        // THEN: Xác nhận đã lưu vào database
        verify(nguoiDungRepository).save(argThat(nd -> {
            assertThat(nd.getMatKhau()).isEqualTo(MAT_KHAU_MA_HOA);
            assertThat(nd.getVaiTro()).isEqualTo(VaiTro.DOC_GIA);
            assertThat(nd.getTrangThai()).isEqualTo(TrangThaiNguoiDung.HOAT_DONG);
            return true;
        }));
    }

    @Test
    @DisplayName("Đăng ký thất bại - Email đã tồn tại, ném BusinessException")
    void testRegister_EmailExists() {
        // GIVEN: Email đã tồn tại trong DB
        given(nguoiDungRepository.existsByEmail(registerRequestMau.getEmail()))
                .willReturn(true);

        // WHEN & THEN: Gọi đăng ký -> ném BusinessException (EMAIL_DA_TON_TAI)
        assertThatThrownBy(() -> authService.dangKy(registerRequestMau))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.EMAIL_DA_TON_TAI);
                });

        // THEN: Không lưu vào database, không tạo token
        verify(nguoiDungRepository, never()).save(any());
        verify(jwtTokenProvider, never()).taoToken(any());
        verify(passwordEncoder, never()).encode(any());
    }
}
