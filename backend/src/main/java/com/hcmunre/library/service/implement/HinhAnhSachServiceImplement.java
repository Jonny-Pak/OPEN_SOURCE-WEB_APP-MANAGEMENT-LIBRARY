package com.hcmunre.library.service.implement;

import com.hcmunre.library.entity.HinhAnhSach;
import com.hcmunre.library.exception.BusinessException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.HinhAnhSachRepository;
import com.hcmunre.library.service.HinhAnhSachService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HinhAnhSachServiceImplement implements HinhAnhSachService {

    private final HinhAnhSachRepository hinhAnhSachRepository;

    @Override
    // Lấy toàn bộ danh sách hình ảnh trong hệ thống
    public List<HinhAnhSach> getAllHinhAnh() {
        return hinhAnhSachRepository.findAll();
    }

    @Override
    // Lấy danh sách hình ảnh của một đầu sách cụ thể
    public List<HinhAnhSach> getHinhAnhBySach(Long maSach) {
        return hinhAnhSachRepository.findBySach_MaSach(maSach);
    }

    @Override
    // Lấy chi tiết một hình ảnh qua ID, trả lỗi nếu không tìm thấy
    public HinhAnhSach getHinhAnhById(Long id) {
        return hinhAnhSachRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HINH_ANH_SACH_KHONG_TON_TAI));
    }

    @Override
    // Lưu hình ảnh mới vào database
    public HinhAnhSach createHinhAnh(HinhAnhSach hinhAnhSach) {
        return hinhAnhSachRepository.save(hinhAnhSach);
    }

    @Override
    // Xóa hình ảnh khỏi hệ thống
    public void deleteHinhAnh(Long id) {
        if (!hinhAnhSachRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.HINH_ANH_SACH_KHONG_TON_TAI);
        }
        hinhAnhSachRepository.deleteById(id);
    }
}
