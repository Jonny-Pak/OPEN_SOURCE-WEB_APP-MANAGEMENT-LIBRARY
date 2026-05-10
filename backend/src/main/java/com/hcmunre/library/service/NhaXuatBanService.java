package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.NhaXuatBanRequest;
import com.hcmunre.library.dto.response.NhaXuatBanResponse;
import java.util.List;

public interface NhaXuatBanService {
    // Queries
    List<NhaXuatBanResponse> getAllNhaXuatBan();
    List<NhaXuatBanResponse> searchNhaXuatBan(String keyword);
    NhaXuatBanResponse getNhaXuatBanById(Long id);

    // Commands
    NhaXuatBanResponse createNhaXuatBan(NhaXuatBanRequest request);
    NhaXuatBanResponse updateNhaXuatBan(Long id, NhaXuatBanRequest request);
    void deleteNhaXuatBan(Long id);
}
