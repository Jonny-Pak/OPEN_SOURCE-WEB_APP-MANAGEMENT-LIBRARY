package com.hcmunre.library.service.implement;

import com.hcmunre.library.entity.TheLoai;
import com.hcmunre.library.exception.BusinessException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.TheLoaiRepository;
import com.hcmunre.library.service.TheLoaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TheLoaiServiceImplement implements TheLoaiService {

    private final TheLoaiRepository theLoaiRepository;

    @Override
    // Lấy toàn bộ danh sách thể loại từ database
    public List<TheLoai> getAllTheLoai() {
        return theLoaiRepository.findAll();
    }

    @Override
    // Tìm kiếm các thể loại dựa trên từ khóa trong tên
    public List<TheLoai> searchTheLoai(String keyword) {
        return theLoaiRepository.findByTenTheLoaiContainingIgnoreCase(keyword);
    }

    @Override
    // Lấy một thể loại theo ID, trả lỗi nếu không tồn tại
    public TheLoai getTheLoaiById(Long id) {
        return theLoaiRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.THE_LOAI_KHONG_TON_TAI));
    }

    @Override
    // Lưu thông tin thể loại mới vào database
    public TheLoai createTheLoai(TheLoai theLoai) {
        return theLoaiRepository.save(theLoai);
    }

    @Override
    // Cập nhật thông tin thể loại đã có, trả lỗi nếu không tìm thấy
    public TheLoai updateTheLoai(Long id, TheLoai theLoai) {
        if (!theLoaiRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.THE_LOAI_KHONG_TON_TAI);
        }
        theLoai.setMaTheLoai(id);
        return theLoaiRepository.save(theLoai);
    }

    @Override
    // Xóa thể loại khỏi hệ thống dựa trên ID
    public void deleteTheLoai(Long id) {
        if (!theLoaiRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.THE_LOAI_KHONG_TON_TAI);
        }
        theLoaiRepository.deleteById(id);
    }
}
