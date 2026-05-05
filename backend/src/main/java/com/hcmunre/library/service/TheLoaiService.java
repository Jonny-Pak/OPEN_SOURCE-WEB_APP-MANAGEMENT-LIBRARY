package com.hcmunre.library.service;

import com.hcmunre.library.entity.TheLoai;
import java.util.List;

public interface TheLoaiService {
    // Lấy danh sách tất cả thể loại
    List<TheLoai> getAllTheLoai();

    // Tìm kiếm thể loại theo từ khóa
    List<TheLoai> searchTheLoai(String keyword);

    // Lấy thông tin thể loại theo ID
    TheLoai getTheLoaiById(Long id);

    // Tạo mới một thể loại
    TheLoai createTheLoai(TheLoai theLoai);

    // Cập nhật thông tin thể loại
    TheLoai updateTheLoai(Long id, TheLoai theLoai);

    // Xóa thể loại theo ID
    void deleteTheLoai(Long id);
}
