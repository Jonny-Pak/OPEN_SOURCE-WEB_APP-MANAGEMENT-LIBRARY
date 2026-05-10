package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.TacGiaRequest;
import com.hcmunre.library.dto.response.TacGiaResponse;
import java.util.List;

public interface TacGiaService {
    // Queries
    List<TacGiaResponse> getAllTacGia();
    List<TacGiaResponse> searchTacGia(String keyword);
    TacGiaResponse getTacGiaById(Long id);

    // Commands
    TacGiaResponse createTacGia(TacGiaRequest request);
    TacGiaResponse updateTacGia(Long id, TacGiaRequest request);
    void deleteTacGia(Long id);
}
