package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.NhaXuatBanRequest;
import com.hcmunre.library.entity.NhaXuatBan;
import java.util.List;

public interface NhaXuatBanService {
    // Lấy danh sách tất cả nhà xuất bản
    List<NhaXuatBan> getAllNhaXuatBan();
    // Tìm kiếm nhà xuất bản theo từ khóa
    List<NhaXuatBan> searchNhaXuatBan(String keyword);
    // Lấy thông tin nhà xuất bản theo ID
    NhaXuatBan getNhaXuatBanById(Long id);
    // Tạo mới một nhà xuất bản
    NhaXuatBan createNhaXuatBan(NhaXuatBanRequest request);
    // Cập nhật thông tin nhà xuất bản
    NhaXuatBan updateNhaXuatBan(Long id, NhaXuatBanRequest request);
    // Xóa nhà xuất bản theo ID
    void deleteNhaXuatBan(Long id);
}
