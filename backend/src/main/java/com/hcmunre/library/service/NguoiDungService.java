package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.AdminTaoNguoiDungRequest;
import com.hcmunre.library.dto.request.ChangePasswordRequest;
import com.hcmunre.library.dto.request.UpdateProfileRequest;
import com.hcmunre.library.dto.response.NguoiDungResponse;
import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.enums.TrangThaiNguoiDung;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NguoiDungService {
    // Queries
    NguoiDung getNguoiDungActive(UUID maNugoiDung);

    Page<NguoiDungResponse> getAllNguoiDung(Pageable pageable, String keyword, TrangThaiNguoiDung trangThai);

    NguoiDungResponse getMyProfile(UUID maNguoiDung);

    // Commands
    NguoiDungResponse updateProfile(UUID maNguoiDung, UpdateProfileRequest request);

    void changePassword(UUID maNguoiDung, ChangePasswordRequest request);

    void toggleUserStatus(UUID id, TrangThaiNguoiDung trangThai);

    NguoiDungResponse createNguoiDung(AdminTaoNguoiDungRequest request);
    NguoiDungResponse updateNguoiDung(UUID id, UpdateProfileRequest request);
    void deleteNguoiDung(UUID id);

    /** Admin đổi mật khẩu bất kỳ người dùng (không cần OTP) */
    void adminDoiMatKhau(UUID id, String matKhauMoi);
}
