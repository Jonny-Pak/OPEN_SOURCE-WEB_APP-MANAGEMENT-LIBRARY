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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NguoiDungServiceImplement implements NguoiDungService {
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public NguoiDung getNguoiDungActive(UUID maNugoiDung) {
        return nguoiDungRepository.findById(maNugoiDung).orElse(null);
    }

    @Override
    public List<NguoiDungResponse> getAllNguoiDung() {
        return nguoiDungRepository.findAll().stream().map(this::toRespone).collect(Collectors.toList());
    }

    @Override
    public NguoiDungResponse getMyProfile(UUID maNguoiDung) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).orElseThrow(
                () -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI)
        );

        return toRespone(nguoiDung);
    }

    @Override
    public void checkNguoiDungPenalty(UUID maNguoiDung) {

    }

    @Override
    public NguoiDungResponse updateProfile(UUID maNguoiDung, UpdateProfileRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).orElseThrow(
                () -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI)
        );

        nguoiDung.setHoDem(request.getHoDem());
        nguoiDung.setTen(request.getTen());
        nguoiDung.setSoDienThoai(request.getSoDienThoai());

        return toRespone(nguoiDungRepository.save(nguoiDung));
    }

    @Override
    public void changePassword(UUID maNguoiDung, ChangePasswordRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).orElseThrow(
                () -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI)
        );

        if(!passwordEncoder.matches(request.getMatKhauCu(), nguoiDung.getMatKhau())){
            throw new LibraryException(ErrorCode.MAT_KHAU_CU_SAI);
        }

        if(!request.getMatKhauMoi().equals(request.getXacNhanMatKhau())){
            throw new LibraryException(ErrorCode.MAT_KHAU_KHONG_KHOP);
        }

        nguoiDung.setMatKhau(passwordEncoder.encode(request.getMatKhauMoi()));
        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    public void toggleUserStatus(UUID targetUserId, TrangThaiNguoiDung newTrangThai) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(targetUserId).orElseThrow(
                () -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI)
        );

        nguoiDung.setTrangThai(newTrangThai);
        nguoiDungRepository.save(nguoiDung);
    }

    private NguoiDungResponse toRespone(NguoiDung nguoiDung){
        return NguoiDungResponse.builder()
                .maNguoiDung(nguoiDung.getMaNguoiDung())
                .hoDem(nguoiDung.getHoDem())
                .ten(nguoiDung.getTen())
                .email(nguoiDung.getEmail())
                .soDienThoai(nguoiDung.getSoDienThoai())
                .vaiTro(nguoiDung.getVaiTro())
                .trangThai(nguoiDung.getTrangThai())
                .build();
    }
}
