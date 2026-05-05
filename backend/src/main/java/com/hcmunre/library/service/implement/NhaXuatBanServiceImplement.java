package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.NhaXuatBanRequest;
import com.hcmunre.library.entity.NhaXuatBan;
import com.hcmunre.library.exception.BusinessException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.NhaXuatBanRepository;
import com.hcmunre.library.service.NhaXuatBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NhaXuatBanServiceImplement implements NhaXuatBanService {

    private final NhaXuatBanRepository nhaXuatBanRepository;

    @Override
    // Lấy toàn bộ danh sách nhà xuất bản từ database
    public List<NhaXuatBan> getAllNhaXuatBan() {
        return nhaXuatBanRepository.findAll();
    }

    @Override
    // Tìm kiếm các nhà xuất bản dựa trên từ khóa trong tên
    public List<NhaXuatBan> searchNhaXuatBan(String keyword) {
        return nhaXuatBanRepository.findByTenNhaXuatBanContainingIgnoreCase(keyword);
    }

    @Override
    // Lấy một nhà xuất bản theo ID, trả lỗi nếu không tồn tại
    public NhaXuatBan getNhaXuatBanById(Long id) {
        return nhaXuatBanRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NHA_XUAT_BAN_KHONG_TON_TAI));
    }

    @Override
    // Tạo mới nhà xuất bản — kiểm tra email và SĐT unique
    public NhaXuatBan createNhaXuatBan(NhaXuatBanRequest request) {
        // Validate email unique
        if (nhaXuatBanRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_DA_TON_TAI);
        }
        // Validate SĐT unique
        if (nhaXuatBanRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new BusinessException(ErrorCode.SDT_DA_TON_TAI);
        }

        NhaXuatBan nhaXuatBan = NhaXuatBan.builder()
                .tenNhaXuatBan(request.getTenNhaXuatBan())
                .diaChi(request.getDiaChi())
                .soDienThoai(request.getSoDienThoai())
                .email(request.getEmail())
                .build();

        return nhaXuatBanRepository.save(nhaXuatBan);
    }

    @Override
    // Cập nhật nhà xuất bản — kiểm tra email và SĐT unique
    public NhaXuatBan updateNhaXuatBan(Long id, NhaXuatBanRequest request) {
        NhaXuatBan nhaXuatBan = nhaXuatBanRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NHA_XUAT_BAN_KHONG_TON_TAI));

        if (request.getEmail() != null &&
                nhaXuatBanRepository.existsByEmailAndMaNhaXuatBanNot(request.getEmail(), id)) {
            throw new BusinessException(ErrorCode.EMAIL_DA_TON_TAI);
        }
        if (request.getSoDienThoai() != null &&
                nhaXuatBanRepository.existsBySoDienThoaiAndMaNhaXuatBanNot(request.getSoDienThoai(), id)) {
            throw new BusinessException(ErrorCode.SDT_DA_TON_TAI);
        }

        nhaXuatBan.setTenNhaXuatBan(request.getTenNhaXuatBan());
        nhaXuatBan.setDiaChi(request.getDiaChi());
        nhaXuatBan.setSoDienThoai(request.getSoDienThoai());
        nhaXuatBan.setEmail(request.getEmail());

        return nhaXuatBanRepository.save(nhaXuatBan);
    }

    @Override
    // Xóa nhà xuất bản khỏi hệ thống dựa trên ID
    public void deleteNhaXuatBan(Long id) {
        if (!nhaXuatBanRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.NHA_XUAT_BAN_KHONG_TON_TAI);
        }
        nhaXuatBanRepository.deleteById(id);
    }
}
