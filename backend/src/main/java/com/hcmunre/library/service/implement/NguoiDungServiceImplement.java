package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.AdminTaoNguoiDungRequest;
import com.hcmunre.library.dto.request.ChangePasswordRequest;
import com.hcmunre.library.dto.request.UpdateProfileRequest;
import com.hcmunre.library.dto.response.NguoiDungResponse;
import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.enums.LoaiThongBao;
import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.enums.VaiTro;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.repository.NguoiDungRepository;
import com.hcmunre.library.service.EmailOutboxService;
import com.hcmunre.library.service.NguoiDungService;
import com.hcmunre.library.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class NguoiDungServiceImplement implements NguoiDungService {
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailOutboxService emailOutboxService;
    private final ThongBaoService thongBaoService;

    @Override
    public NguoiDung getNguoiDungActive(UUID maNugoiDung) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNugoiDung)
                .orElseThrow(() -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI));

        if (nguoiDung.getTrangThai() == TrangThaiNguoiDung.KHOA) {
            throw new LibraryException(ErrorCode.TAI_KHOAN_BI_KHOA);
        }

        return nguoiDung;
    }

    @Override
    public Page<NguoiDungResponse> getAllNguoiDung(Pageable pageable, String keyword, TrangThaiNguoiDung trangThai) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (trangThai != null) {
                return nguoiDungRepository.searchNguoiDungWithStatus(keyword.trim(), trangThai, pageable).map(this::toRespone);
            } else {
                return nguoiDungRepository.searchNguoiDung(keyword.trim(), pageable).map(this::toRespone);
            }
        } else {
            if (trangThai != null) {
                return nguoiDungRepository.findByNgayXoaIsNullAndTrangThai(trangThai, pageable).map(this::toRespone);
            } else {
                return nguoiDungRepository.findByNgayXoaIsNull(pageable).map(this::toRespone);
            }
        }
    }

    @Override
    public NguoiDungResponse getMyProfile(UUID maNguoiDung) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).orElseThrow(
                () -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI));

        return toRespone(nguoiDung);
    }

    @Override
    public NguoiDungResponse updateProfile(UUID maNguoiDung, UpdateProfileRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).orElseThrow(
                () -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI));

        nguoiDung.setHoDem(request.getHoDem());
        nguoiDung.setTen(request.getTen());
        nguoiDung.setSoDienThoai(request.getSoDienThoai());
        nguoiDung.setNgaySinh(request.getNgaySinh());
        nguoiDung.setGioiTinh(request.getGioiTinh());
        if(request.getCccd() != null) nguoiDung.setCccd(request.getCccd());
        if(request.getDiaChi() != null) nguoiDung.setDiaChi(request.getDiaChi());
        if(request.getAvatar() != null) nguoiDung.setAvatar(request.getAvatar());

        return toRespone(nguoiDungRepository.save(nguoiDung));
    }

    @Override
    public void changePassword(UUID maNguoiDung, ChangePasswordRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).orElseThrow(
                () -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI));

        if (!passwordEncoder.matches(request.getMatKhauCu(), nguoiDung.getMatKhau())) {
            throw new LibraryException(ErrorCode.MAT_KHAU_CU_SAI);
        }

        if (!request.getMatKhauMoi().equals(request.getXacNhanMatKhau())) {
            throw new LibraryException(ErrorCode.MAT_KHAU_KHONG_KHOP);
        }

        nguoiDung.setMatKhau(passwordEncoder.encode(request.getMatKhauMoi()));
        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    @Transactional
    public void toggleUserStatus(UUID targetUserId, TrangThaiNguoiDung newTrangThai) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(targetUserId)
                .orElseThrow(() -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI));
        TrangThaiNguoiDung oldTrangThai = nguoiDung.getTrangThai();
        nguoiDung.setTrangThai(newTrangThai);
        nguoiDungRepository.save(nguoiDung);

        // Gửi email & thông báo khi kích hoạt hoặc khóa tài khoản
        if (newTrangThai == TrangThaiNguoiDung.HOAT_DONG && oldTrangThai == TrangThaiNguoiDung.CHUA_KICH_HOAT) {
            String noiDungEmail = xayDungEmailKichHoat(nguoiDung);
            emailOutboxService.lenLichGuiEmail(nguoiDung.getEmail(),
                    "[Thư viện] Tài khoản của bạn đã được kích hoạt", noiDungEmail);
            thongBaoService.taoThongBao(nguoiDung.getMaNguoiDung(),
                    "Tài khoản đã được kích hoạt",
                    "Chúc mừng! Tài khoản thư viện của bạn đã được kích hoạt thành công. Bạn có thể đăng nhập ngay.",
                    LoaiThongBao.HE_THONG);
        } else if (newTrangThai == TrangThaiNguoiDung.HOAT_DONG && oldTrangThai == TrangThaiNguoiDung.KHOA) {
            String noiDungEmail = xayDungEmailMoKhoa(nguoiDung);
            emailOutboxService.lenLichGuiEmail(nguoiDung.getEmail(),
                    "[Thư viện] Tài khoản của bạn đã được mở khóa", noiDungEmail);
            thongBaoService.taoThongBao(nguoiDung.getMaNguoiDung(),
                    "Tài khoản đã được mở khóa",
                    "Tài khoản thư viện của bạn đã được mở khóa. Bạn có thể tiếp tục sử dụng dịch vụ.",
                    LoaiThongBao.HE_THONG);
        } else if (newTrangThai == TrangThaiNguoiDung.KHOA) {
            String noiDungEmail = xayDungEmailKhoaTaiKhoan(nguoiDung);
            emailOutboxService.lenLichGuiEmail(nguoiDung.getEmail(),
                    "[Thư viện] Tài khoản của bạn đã bị khóa", noiDungEmail);
            thongBaoService.taoThongBao(nguoiDung.getMaNguoiDung(),
                    "Tài khoản bị khóa",
                    "Tài khoản thư viện của bạn đã bị khóa. Vui lòng liên hệ thư viện để biết thêm chi tiết.",
                    LoaiThongBao.CANH_BAO);
        }
    }

    @Override
    @Transactional
    public NguoiDungResponse createNguoiDung(AdminTaoNguoiDungRequest request) {
        if (nguoiDungRepository.existsByEmail(request.getEmail())) {
            throw new LibraryException(ErrorCode.EMAIL_DA_TON_TAI);
        }
        if (request.getSoDienThoai() != null && !request.getSoDienThoai().isBlank()
                && nguoiDungRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new LibraryException(ErrorCode.SDT_DA_TON_TAI);
        }
        if (request.getCccd() != null && !request.getCccd().isBlank()
                && nguoiDungRepository.existsByCccd(request.getCccd())) {
            throw new LibraryException(ErrorCode.CCCD_DA_TON_TAI);
        }

        VaiTro vaiTro = request.getVaiTro() != null ? request.getVaiTro() : VaiTro.DOC_GIA;
        TrangThaiNguoiDung trangThai = request.getTrangThai();
        if (trangThai == null) {
            trangThai = (vaiTro == VaiTro.DOC_GIA) ? TrangThaiNguoiDung.CHUA_KICH_HOAT : TrangThaiNguoiDung.HOAT_DONG;
        }

        // Nếu không nhập mật khẩu, dùng mật khẩu mặc định
        String rawPassword = (request.getMatKhau() == null || request.getMatKhau().isBlank())
                ? "123" : request.getMatKhau();

        NguoiDung nguoiDung = NguoiDung.builder()
                .hoDem(request.getHoDem())
                .ten(request.getTen())
                .email(request.getEmail())
                .soDienThoai(request.getSoDienThoai())
                .matKhau(passwordEncoder.encode(rawPassword))
                .ngaySinh(request.getNgaySinh())
                .gioiTinh(request.getGioiTinh())
                .cccd(request.getCccd())
                .vaiTro(vaiTro)
                .trangThai(trangThai)
                .build();
        return toRespone(nguoiDungRepository.save(nguoiDung));
    }

    /**
     * Admin update of any user by ID. Delegates to updateProfile — same logic.
     * Kept in interface for API separation; implementation unified here.
     */
    @Override
    @Transactional
    public NguoiDungResponse updateNguoiDung(UUID id, UpdateProfileRequest request) {
        return updateProfile(id, request);
    }

    @Override
    @Transactional
    public void deleteNguoiDung(UUID id) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI));
        // Soft delete only: set ngayXoa + lock account. Hard delete is forbidden for NguoiDung.
        nguoiDung.setNgayXoa(LocalDateTime.now());
        nguoiDung.setTrangThai(TrangThaiNguoiDung.KHOA);
        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    @Transactional
    public void adminDoiMatKhau(UUID id, String matKhauMoi) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI));
        nguoiDung.setMatKhau(passwordEncoder.encode(matKhauMoi));
        nguoiDungRepository.save(nguoiDung);
    }

    private NguoiDungResponse toRespone(NguoiDung nguoiDung) {
        return NguoiDungResponse.builder()
                .maNguoiDung(nguoiDung.getMaNguoiDung())
                .hoDem(nguoiDung.getHoDem())
                .ten(nguoiDung.getTen())
                .email(nguoiDung.getEmail())
                .soDienThoai(nguoiDung.getSoDienThoai())
                .ngaySinh(nguoiDung.getNgaySinh())
                .gioiTinh(nguoiDung.getGioiTinh())
                .cccd(nguoiDung.getCccd())
                .diaChi(nguoiDung.getDiaChi())
                .vaiTro(nguoiDung.getVaiTro())
                .trangThai(nguoiDung.getTrangThai())
                .avatar(nguoiDung.getAvatar())
                .ngayTao(nguoiDung.getNgayTao())
                .build();
    }

    // ===== Email Templates =====

    private String xayDungEmailKichHoat(NguoiDung nd) {
        return "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto'>" +
                "<h2 style='color:#16a34a'>🎉 Tài khoản đã được kích hoạt!</h2>" +
                "<p>Chào <b>" + nd.getHoTen() + "</b>,</p>" +
                "<p>Tài khoản thư viện của bạn đã được kích hoạt thành công. Bạn có thể đăng nhập ngay bây giờ.</p>" +
                "<p>Chúc bạn tìm được nhiều cuốn sách hay!</p>" +
                "<p>Trân trọng,<br>Hệ thống Thư viện</p></div>";
    }

    private String xayDungEmailMoKhoa(NguoiDung nd) {
        return "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto'>" +
                "<h2 style='color:#2563eb'>🔓 Tài khoản đã được mở khóa</h2>" +
                "<p>Chào <b>" + nd.getHoTen() + "</b>,</p>" +
                "<p>Tài khoản thư viện của bạn đã được mở khóa. Bạn có thể tiếp tục sử dụng dịch vụ thư viện.</p>" +
                "<p>Trân trọng,<br>Hệ thống Thư viện</p></div>";
    }

    private String xayDungEmailKhoaTaiKhoan(NguoiDung nd) {
        return "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto'>" +
                "<h2 style='color:#dc2626'>🔒 Tài khoản bị khóa</h2>" +
                "<p>Chào <b>" + nd.getHoTen() + "</b>,</p>" +
                "<p>Tài khoản thư viện của bạn đã bị khóa. Vui lòng liên hệ thư viện để biết thêm chi tiết và được hỗ trợ.</p>" +
                "<p>Trân trọng,<br>Hệ thống Thư viện</p></div>";
    }
}
