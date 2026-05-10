package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.CuonSachRequest;
import com.hcmunre.library.dto.response.CuonSachResponse;
import com.hcmunre.library.entity.CuonSach;
import com.hcmunre.library.entity.Sach;
import com.hcmunre.library.enums.TrangThaiCuonSach;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.CuonSachRepository;
import com.hcmunre.library.repository.SachRepository;
import com.hcmunre.library.service.CuonSachService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuonSachServiceImplement implements CuonSachService {

    private final CuonSachRepository cuonSachRepository;
    private final SachRepository sachRepository;

    @Override
    public List<CuonSachResponse> getAllCuonSach() {
        return cuonSachRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<CuonSachResponse> getCuonSachBySach(Long maSach) {
        return cuonSachRepository.findBySach_MaSach(maSach).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public CuonSachResponse getCuonSachById(Long id) {
        return cuonSachRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new LibraryException(ErrorCode.CUON_SACH_KHONG_TON_TAI));
    }

    @Override
    public CuonSachResponse createCuonSach(CuonSachRequest request) {
        // Validate mã vạch unique
        if (cuonSachRepository.existsByMaVach(request.getMaVach())) {
            throw new LibraryException(ErrorCode.MA_VACH_DA_TON_TAI);
        }

        Sach sach = sachRepository.findById(request.getMaSach())
                .orElseThrow(() -> new LibraryException(ErrorCode.SACH_KHONG_TON_TAI));

        CuonSach cuonSach = CuonSach.builder()
                .maVach(request.getMaVach())
                .viTriKe(request.getViTriKe())
                .trangThai(request.getTrangThai())
                .tinhTrangVatLy(request.getTinhTrangVatLy())
                .ghiChuBaoTri(request.getGhiChuBaoTri())
                .sach(sach)
                .build();

        return toResponse(cuonSachRepository.save(cuonSach));
    }

    @Override
    public CuonSachResponse updateCuonSach(Long id, CuonSachRequest request) {
        CuonSach cuonSach = cuonSachRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.CUON_SACH_KHONG_TON_TAI));

        // Validate mã vạch unique (trừ cuốn đang update)
        if (request.getMaVach() != null &&
                cuonSachRepository.existsByMaVachAndMaCuonSachNot(request.getMaVach(), id)) {
            throw new LibraryException(ErrorCode.MA_VACH_DA_TON_TAI);
        }

        cuonSach.setMaVach(request.getMaVach());
        cuonSach.setViTriKe(request.getViTriKe());
        cuonSach.setTrangThai(request.getTrangThai());
        cuonSach.setTinhTrangVatLy(request.getTinhTrangVatLy());
        cuonSach.setGhiChuBaoTri(request.getGhiChuBaoTri());

        if (request.getMaSach() != null) {
            Sach sach = sachRepository.findById(request.getMaSach())
                    .orElseThrow(() -> new LibraryException(ErrorCode.SACH_KHONG_TON_TAI));
            cuonSach.setSach(sach);
        }

        return toResponse(cuonSachRepository.save(cuonSach));
    }

    @Override
    // Xóa bản sao sách khỏi hệ thống
    public void deleteCuonSach(Long id) {
        if (!cuonSachRepository.existsById(id)) {
            throw new LibraryException(ErrorCode.CUON_SACH_KHONG_TON_TAI);
        }
        cuonSachRepository.deleteById(id);
    }

    @Override
    public CuonSach getCuonSachAvailable(Long maSach) {
        List<CuonSach> availableSach = cuonSachRepository.findBySach_MaSachAndTrangThai(maSach, TrangThaiCuonSach.SAN_SANG);
        if(availableSach.isEmpty()){
            throw new LibraryException(ErrorCode.CUON_SACH_KHONG_SAN_SANG);
        }
        return availableSach.get(0);
    }

    @Override
    @Transactional
    public void updateTrangThaiCuonSach(Long maCuonSach, TrangThaiCuonSach trangThai) {
        CuonSach cuonSach = cuonSachRepository.findById(maCuonSach)
                .orElseThrow(() -> new LibraryException(ErrorCode.CUON_SACH_KHONG_TON_TAI));
        cuonSach.setTrangThai(trangThai);
        cuonSachRepository.save(cuonSach);
    }

    private CuonSachResponse toResponse(CuonSach cuonSach) {
        return CuonSachResponse.builder()
                .maCuonSach(cuonSach.getMaCuonSach())
                .maSach(cuonSach.getSach().getMaSach())
                .tenSach(cuonSach.getSach().getTenSach())
                .maVach(cuonSach.getMaVach())
                .viTriKe(cuonSach.getViTriKe())
                .trangThai(cuonSach.getTrangThai())
                .tinhTrangVatLy(cuonSach.getTinhTrangVatLy())
                .ghiChuBaoTri(cuonSach.getGhiChuBaoTri())
                .build();
    }
}
