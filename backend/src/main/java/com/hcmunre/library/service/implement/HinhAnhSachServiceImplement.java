package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.HinhAnhSachRequest;
import com.hcmunre.library.dto.response.HinhAnhSachResponse;
import com.hcmunre.library.entity.HinhAnhSach;
import com.hcmunre.library.entity.Sach;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.HinhAnhSachRepository;
import com.hcmunre.library.repository.SachRepository;
import com.hcmunre.library.service.HinhAnhSachService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HinhAnhSachServiceImplement implements HinhAnhSachService {

    private final HinhAnhSachRepository hinhAnhSachRepository;
    private final SachRepository sachRepository;

    @Override
    public List<HinhAnhSachResponse> getAllHinhAnh() {
        return hinhAnhSachRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HinhAnhSachResponse> getHinhAnhBySach(Long maSach) {
        return hinhAnhSachRepository.findBySach_MaSach(maSach).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HinhAnhSachResponse getHinhAnhById(Long id) {
        HinhAnhSach hinhAnh = hinhAnhSachRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.HINH_ANH_SACH_KHONG_TON_TAI));
        return mapToResponse(hinhAnh);
    }

    @Override
    public HinhAnhSachResponse createHinhAnh(HinhAnhSachRequest request) {
        Sach sach = sachRepository.findById(request.getMaSach())
                .orElseThrow(() -> new LibraryException(ErrorCode.SACH_KHONG_TON_TAI));
                
        HinhAnhSach hinhAnhSach = HinhAnhSach.builder()
                .duongDan(request.getDuongDan())
                .loaiHinhAnh(request.getLoaiHinhAnh())
                .thuTuHienThi(request.getThuTuHienThi())
                .sach(sach)
                .build();
                
        HinhAnhSach saved = hinhAnhSachRepository.save(hinhAnhSach);
        return mapToResponse(saved);
    }

    @Override
    public void deleteHinhAnh(Long id) {
        if (!hinhAnhSachRepository.existsById(id)) {
            throw new LibraryException(ErrorCode.HINH_ANH_SACH_KHONG_TON_TAI);
        }
        hinhAnhSachRepository.deleteById(id);
    }
    
    private HinhAnhSachResponse mapToResponse(HinhAnhSach entity) {
        return HinhAnhSachResponse.builder()
                .maHinhAnh(entity.getMaHinhAnh())
                .duongDan(entity.getDuongDan())
                .loaiHinhAnh(entity.getLoaiHinhAnh())
                .thuTuHienThi(entity.getThuTuHienThi())
                .maSach(entity.getSach() != null ? entity.getSach().getMaSach() : null)
                .build();
    }
}
