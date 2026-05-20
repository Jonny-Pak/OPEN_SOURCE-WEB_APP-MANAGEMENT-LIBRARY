package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.NhaXuatBanRequest;
import com.hcmunre.library.entity.NhaXuatBan;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.NhaXuatBanRepository;
import com.hcmunre.library.service.NhaXuatBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.hcmunre.library.dto.response.NhaXuatBanResponse;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NhaXuatBanServiceImplement implements NhaXuatBanService {

    private final NhaXuatBanRepository nhaXuatBanRepository;

    @Override
    public List<NhaXuatBanResponse> getAllNhaXuatBan() {
        return nhaXuatBanRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<NhaXuatBanResponse> searchNhaXuatBan(String keyword) {
        return nhaXuatBanRepository.findByTenNhaXuatBanContainingIgnoreCase(keyword).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public NhaXuatBanResponse getNhaXuatBanById(Long id) {
        return nhaXuatBanRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new LibraryException(ErrorCode.NHA_XUAT_BAN_KHONG_TON_TAI));
    }

    @Override
    public NhaXuatBanResponse createNhaXuatBan(NhaXuatBanRequest request) {
        if (nhaXuatBanRepository.existsByEmail(request.getEmail())) {
            throw new LibraryException(ErrorCode.EMAIL_DA_TON_TAI);
        }
        if (nhaXuatBanRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new LibraryException(ErrorCode.SDT_DA_TON_TAI);
        }

        NhaXuatBan nhaXuatBan = NhaXuatBan.builder()
                .tenNhaXuatBan(request.getTenNhaXuatBan())
                .diaChi(request.getDiaChi())
                .soDienThoai(request.getSoDienThoai())
                .email(request.getEmail())
                .build();

        return toResponse(nhaXuatBanRepository.save(nhaXuatBan));
    }

    @Override
    public NhaXuatBanResponse updateNhaXuatBan(Long id, NhaXuatBanRequest request) {
        NhaXuatBan nhaXuatBan = nhaXuatBanRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.NHA_XUAT_BAN_KHONG_TON_TAI));

        if (request.getEmail() != null &&
                nhaXuatBanRepository.existsByEmailAndMaNhaXuatBanNot(request.getEmail(), id)) {
            throw new LibraryException(ErrorCode.EMAIL_DA_TON_TAI);
        }
        if (request.getSoDienThoai() != null &&
                nhaXuatBanRepository.existsBySoDienThoaiAndMaNhaXuatBanNot(request.getSoDienThoai(), id)) {
            throw new LibraryException(ErrorCode.SDT_DA_TON_TAI);
        }

        nhaXuatBan.setTenNhaXuatBan(request.getTenNhaXuatBan());
        nhaXuatBan.setDiaChi(request.getDiaChi());
        nhaXuatBan.setSoDienThoai(request.getSoDienThoai());
        nhaXuatBan.setEmail(request.getEmail());

        return toResponse(nhaXuatBanRepository.save(nhaXuatBan));
    }

    @Override
    public void deleteNhaXuatBan(Long id) {
        if (!nhaXuatBanRepository.existsById(id)) {
            throw new LibraryException(ErrorCode.NHA_XUAT_BAN_KHONG_TON_TAI);
        }
        nhaXuatBanRepository.deleteById(id);
    }

    private NhaXuatBanResponse toResponse(NhaXuatBan nhaXuatBan) {
        return NhaXuatBanResponse.builder()
                .maNhaXuatBan(nhaXuatBan.getMaNhaXuatBan())
                .tenNhaXuatBan(nhaXuatBan.getTenNhaXuatBan())
                .diaChi(nhaXuatBan.getDiaChi())
                .soDienThoai(nhaXuatBan.getSoDienThoai())
                .email(nhaXuatBan.getEmail())
                .build();
    }
}
