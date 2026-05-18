package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.ChangePasswordRequest;
import com.hcmunre.library.dto.request.UpdateProfileRequest;
import com.hcmunre.library.dto.response.NguoiDungResponse;
import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.enums.TrangThaiNguoiDung;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.repository.NguoiDungRepository;
import com.hcmunre.library.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class NguoiDungServiceImplement implements NguoiDungService {
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

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
        nguoiDung.setTrangThai(newTrangThai);
        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    @Transactional
    public NguoiDungResponse createNguoiDung(com.hcmunre.library.dto.request.AdminTaoNguoiDungRequest request) {
        if (nguoiDungRepository.existsByEmail(request.getEmail())) {
            throw new LibraryException(ErrorCode.EMAIL_DA_TON_TAI);
        }
        if (nguoiDungRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new LibraryException(ErrorCode.SDT_DA_TON_TAI);
        }

        com.hcmunre.library.enums.VaiTro vaiTro = request.getVaiTro() != null ? request.getVaiTro() : com.hcmunre.library.enums.VaiTro.DOC_GIA;
        TrangThaiNguoiDung trangThai = request.getTrangThai();
        if (trangThai == null) {
            trangThai = (vaiTro == com.hcmunre.library.enums.VaiTro.DOC_GIA) ? TrangThaiNguoiDung.CHUA_KICH_HOAT : TrangThaiNguoiDung.HOAT_DONG;
        }

        NguoiDung nguoiDung = NguoiDung.builder()
                .hoDem(request.getHoDem())
                .ten(request.getTen())
                .email(request.getEmail())
                .soDienThoai(request.getSoDienThoai())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .vaiTro(vaiTro)
                .trangThai(trangThai)
                .build();
        return toRespone(nguoiDungRepository.save(nguoiDung));
    }

    @Override
    @Transactional
    public NguoiDungResponse updateNguoiDung(UUID id, UpdateProfileRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI));
        
        nguoiDung.setHoDem(request.getHoDem());
        nguoiDung.setTen(request.getTen());
        nguoiDung.setSoDienThoai(request.getSoDienThoai());
        nguoiDung.setNgaySinh(request.getNgaySinh());
        nguoiDung.setGioiTinh(request.getGioiTinh());
        nguoiDung.setCccd(request.getCccd());
        nguoiDung.setDiaChi(request.getDiaChi());
        nguoiDung.setAvatar(request.getAvatar());

        return toRespone(nguoiDungRepository.save(nguoiDung));
    }

    @Override
    @Transactional
    public void deleteNguoiDung(UUID id) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI));
        try {
            nguoiDungRepository.delete(nguoiDung);
            nguoiDungRepository.flush(); // Force database to evaluate constraints immediately
        } catch (Exception e) {
            nguoiDung.setNgayXoa(java.time.LocalDateTime.now());
            nguoiDung.setTrangThai(TrangThaiNguoiDung.KHOA);
            nguoiDungRepository.save(nguoiDung);
        }
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
}
