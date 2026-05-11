package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.ChangePasswordRequest;
import com.hcmunre.library.dto.request.UpdateProfileRequest;
import com.hcmunre.library.dto.response.NguoiDungResponse;
import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.enums.TrangThaiNguoiDung;

import java.util.List;
import java.util.UUID;

public interface NguoiDungService {
    // Queries
    NguoiDung getNguoiDungActive(UUID maNugoiDung);
    List<NguoiDungResponse> getAllNguoiDung();
    NguoiDungResponse getMyProfile(UUID maNguoiDung);


    // Commands)
    void checkNguoiDungPenalty(UUID maNguoiDung);
    NguoiDungResponse updateProfile(UUID maNguoiDung, UpdateProfileRequest request);
    void changePassword(UUID maNguoiDung, ChangePasswordRequest request);
    void toggleUserStatus(UUID targetUserId, TrangThaiNguoiDung newTrangThai);
}
