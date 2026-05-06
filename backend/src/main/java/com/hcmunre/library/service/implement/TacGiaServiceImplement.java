package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.TacGiaRequest;
import com.hcmunre.library.entity.TacGia;
import com.hcmunre.library.exception.BusinessException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.TacGiaRepository;
import com.hcmunre.library.service.TacGiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TacGiaServiceImplement implements TacGiaService {

    private final TacGiaRepository tacGiaRepository;

    @Override
    // Lấy toàn bộ danh sách tác giả từ database
    public List<TacGia> getAllTacGia() {
        return tacGiaRepository.findAll();
    }

    @Override
    // Tìm kiếm các tác giả dựa trên từ khóa trong tên
    public List<TacGia> searchTacGia(String keyword) {
        return tacGiaRepository.findByTenContainingIgnoreCase(keyword);
    }

    @Override
    // Lấy một tác giả theo ID, trả lỗi nếu không tồn tại
    public TacGia getTacGiaById(Long id) {
        return tacGiaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TAC_GIA_KHONG_TON_TAI));
    }

    @Override
    // Tạo mới tác giả
    public TacGia createTacGia(TacGiaRequest request) {
        TacGia tacGia = TacGia.builder()
                .hoDem(request.getHoDem())
                .ten(request.getTen())
                .tieuSu(request.getTieuSu())
                .ngayXoa(null)
                .build();
        return tacGiaRepository.save(tacGia);
    }

    @Override
    // Cập nhật thông tin tác giả đã có
    public TacGia updateTacGia(Long id, TacGiaRequest request) {
        TacGia tacGia = tacGiaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TAC_GIA_KHONG_TON_TAI));

        tacGia.setHoDem(request.getHoDem());
        tacGia.setTen(request.getTen());
        tacGia.setTieuSu(request.getTieuSu());

        return tacGiaRepository.save(tacGia);
    }

    @Override
    // Xóa tác giả khỏi hệ thống dựa trên ID
    public void deleteTacGia(Long id) {
        if (!tacGiaRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.TAC_GIA_KHONG_TON_TAI);
        }
        tacGiaRepository.deleteById(id);
    }
}
