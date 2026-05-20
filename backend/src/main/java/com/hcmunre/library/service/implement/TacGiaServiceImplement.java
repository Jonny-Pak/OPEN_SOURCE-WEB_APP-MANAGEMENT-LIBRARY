package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.TacGiaRequest;
import com.hcmunre.library.entity.TacGia;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.TacGiaRepository;
import com.hcmunre.library.service.TacGiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import com.hcmunre.library.dto.response.TacGiaResponse;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TacGiaServiceImplement implements TacGiaService {

    private final TacGiaRepository tacGiaRepository;

    @Override
    public List<TacGiaResponse> getAllTacGia() {
        return tacGiaRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<TacGiaResponse> searchTacGia(String keyword) {
        return tacGiaRepository.findByTenContainingIgnoreCase(keyword).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public TacGiaResponse getTacGiaById(Long id) {
        return tacGiaRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new LibraryException(ErrorCode.TAC_GIA_KHONG_TON_TAI));
    }

    @Override
    public TacGiaResponse createTacGia(TacGiaRequest request) {
        TacGia tacGia = TacGia.builder()
                .hoDem(request.getHoDem())
                .ten(request.getTen())
                .tieuSu(request.getTieuSu())
                .hinhAnh(request.getHinhAnh())
                .ngayXoa(null)
                .build();
        return toResponse(tacGiaRepository.save(tacGia));
    }

    @Override
    public TacGiaResponse updateTacGia(Long id, TacGiaRequest request) {
        TacGia tacGia = tacGiaRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.TAC_GIA_KHONG_TON_TAI));

        tacGia.setHoDem(request.getHoDem());
        tacGia.setTen(request.getTen());
        tacGia.setTieuSu(request.getTieuSu());
        tacGia.setHinhAnh(request.getHinhAnh());

        return toResponse(tacGiaRepository.save(tacGia));
    }

    @Override
    public void deleteTacGia(Long id) {
        if (!tacGiaRepository.existsById(id)) {
            throw new LibraryException(ErrorCode.TAC_GIA_KHONG_TON_TAI);
        }
        tacGiaRepository.deleteById(id);
    }

    private TacGiaResponse toResponse(TacGia tacGia) {
        return TacGiaResponse.builder()
                .maTacGia(tacGia.getMaTacGia())
                .hoDem(tacGia.getHoDem())
                .ten(tacGia.getTen())
                .tieuSu(tacGia.getTieuSu())
                .hinhAnh(tacGia.getHinhAnh())
                .build();
    }
}
