package com.hcmunre.library.service;

import com.hcmunre.library.entity.HinhAnhSach;
import java.util.List;

public interface HinhAnhSachService {
    // Lấy danh sách tất cả hình ảnh
    List<HinhAnhSach> getAllHinhAnh();

    // Lấy danh sách hình ảnh thuộc về một đầu sách
    List<HinhAnhSach> getHinhAnhBySach(Long maSach);

    // Lấy thông tin hình ảnh theo ID
    HinhAnhSach getHinhAnhById(Long id);

    // Lưu thông tin hình ảnh mới
    HinhAnhSach createHinhAnh(HinhAnhSach hinhAnhSach);

    // Xóa hình ảnh theo ID
    void deleteHinhAnh(Long id);
}
