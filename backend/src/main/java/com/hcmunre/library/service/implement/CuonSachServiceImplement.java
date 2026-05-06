package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.CuonSachRequest;
import com.hcmunre.library.entity.CuonSach;
import com.hcmunre.library.entity.Sach;
import com.hcmunre.library.enums.TrangThaiCuonSach;
import com.hcmunre.library.exception.BusinessException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.CuonSachRepository;
import com.hcmunre.library.repository.SachRepository;
import com.hcmunre.library.service.CuonSachService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuonSachServiceImplement implements CuonSachService {

    private final CuonSachRepository cuonSachRepository;
    private final SachRepository sachRepository;

    @Override
    // Lấy toàn bộ danh sách bản sao sách
    public List<CuonSach> getAllCuonSach() {
        return cuonSachRepository.findAll();
    }

    @Override
    // Lấy danh sách các bản sao thuộc về một đầu sách nhất định
    public List<CuonSach> getCuonSachBySach(Long maSach) {
        return cuonSachRepository.findBySach_MaSach(maSach);
    }

    @Override
    // Lấy thông tin bản sao theo ID, trả lỗi nếu không tồn tại
    public CuonSach getCuonSachById(Long id) {
        return cuonSachRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUON_SACH_KHONG_TON_TAI));
    }

    @Override
    // Tạo mới bản sao sách — kiểm tra mã vạch unique trước khi lưu
    public CuonSach createCuonSach(CuonSachRequest request) {
        // Validate mã vạch unique
        if (cuonSachRepository.existsByMaVach(request.getMaVach())) {
            throw new BusinessException(ErrorCode.MA_VACH_DA_TON_TAI);
        }

        Sach sach = sachRepository.findById(request.getMaSach())
                .orElseThrow(() -> new BusinessException(ErrorCode.SACH_KHONG_TON_TAI));

        CuonSach cuonSach = CuonSach.builder()
                .maVach(request.getMaVach())
                .viTriKe(request.getViTriKe())
                .trangThai(request.getTrangThai())
                .tinhTrangVatLy(request.getTinhTrangVatLy())
                .ghiChuBaoTri(request.getGhiChuBaoTri())
                .sach(sach)
                .build();

        return cuonSachRepository.save(cuonSach);
    }

    @Override
    // Cập nhật thông tin bản sao sách — kiểm tra mã vạch unique (trừ chính nó)
    public CuonSach updateCuonSach(Long id, CuonSachRequest request) {
        CuonSach cuonSach = cuonSachRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUON_SACH_KHONG_TON_TAI));

        // Validate mã vạch unique (trừ cuốn đang update)
        if (request.getMaVach() != null &&
                cuonSachRepository.existsByMaVachAndMaCuonSachNot(request.getMaVach(), id)) {
            throw new BusinessException(ErrorCode.MA_VACH_DA_TON_TAI);
        }

        cuonSach.setMaVach(request.getMaVach());
        cuonSach.setViTriKe(request.getViTriKe());
        cuonSach.setTrangThai(request.getTrangThai());
        cuonSach.setTinhTrangVatLy(request.getTinhTrangVatLy());
        cuonSach.setGhiChuBaoTri(request.getGhiChuBaoTri());

        if (request.getMaSach() != null) {
            Sach sach = sachRepository.findById(request.getMaSach())
                    .orElseThrow(() -> new BusinessException(ErrorCode.SACH_KHONG_TON_TAI));
            cuonSach.setSach(sach);
        }

        return cuonSachRepository.save(cuonSach);
    }

    @Override
    // Xóa bản sao sách khỏi hệ thống
    public void deleteCuonSach(Long id) {
        if (!cuonSachRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.CUON_SACH_KHONG_TON_TAI);
        }
        cuonSachRepository.deleteById(id);
    }

    @Override
    public CuonSach getCuonSachAvailable(Long maSach) {
        // TODO: implement - tìm cuốn sách có trạng thái SẴN SÀNG
        return null;
    }

    @Override
    public void updateTrangThaiCuonSach(Long maCuonSach, TrangThaiCuonSach trangThai) {
        // TODO: implement - cập nhật trạng thái cuốn sách (ĐANG_MƯỢN, SẴN_SÀNG, etc.)
    }
}
