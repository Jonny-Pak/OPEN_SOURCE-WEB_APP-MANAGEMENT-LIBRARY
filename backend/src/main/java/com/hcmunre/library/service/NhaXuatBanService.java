package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.NhaXuatBanRequest;
import com.hcmunre.library.entity.NhaXuatBan;
import java.util.List;

public interface NhaXuatBanService {
    // Queries
    List<NhaXuatBan> getAllNhaXuatBan();
    List<NhaXuatBan> searchNhaXuatBan(String keyword);
    NhaXuatBan getNhaXuatBanById(Long id);

    // Commands
    NhaXuatBan createNhaXuatBan(NhaXuatBanRequest request);
    NhaXuatBan updateNhaXuatBan(Long id, NhaXuatBanRequest request);
    void deleteNhaXuatBan(Long id);
}
