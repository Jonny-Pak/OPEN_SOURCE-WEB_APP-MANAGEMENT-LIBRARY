package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.TacGiaRequest;
import com.hcmunre.library.entity.TacGia;
import java.util.List;

public interface TacGiaService {
    // Lấy danh sách tất cả tác giả
    List<TacGia> getAllTacGia();
    // Tìm kiếm tác giả theo từ khóa
    List<TacGia> searchTacGia(String keyword);
    // Lấy thông tin tác giả theo ID
    TacGia getTacGiaById(Long id);
    // Tạo mới một tác giả
    TacGia createTacGia(TacGiaRequest request);
    // Cập nhật thông tin tác giả
    TacGia updateTacGia(Long id, TacGiaRequest request);
    // Xóa tác giả theo ID
    void deleteTacGia(Long id);
}
