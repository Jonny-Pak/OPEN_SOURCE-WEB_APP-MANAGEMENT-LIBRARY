package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.SachRequest;
import com.hcmunre.library.entity.Sach;
import java.util.List;

public interface SachService {
    // Lấy danh sách tất cả đầu sách
    List<Sach> getAllSach();

    // Tìm kiếm đầu sách theo từ khóa
    List<Sach> searchSach(String keyword);

    // Lấy thông tin đầu sách theo ID
    Sach getSachById(Long id);

    // Tạo mới một đầu sách
    Sach createSach(SachRequest request);

    // Cập nhật thông tin đầu sách
    Sach updateSach(Long id, SachRequest request);

    // Xóa đầu sách theo ID
    void deleteSach(Long id);
}
