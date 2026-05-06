package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.TacGiaRequest;
import com.hcmunre.library.entity.TacGia;
import java.util.List;

public interface TacGiaService {
    // Queries
    List<TacGia> getAllTacGia();
    List<TacGia> searchTacGia(String keyword);
    TacGia getTacGiaById(Long id);

    // Commands
    TacGia createTacGia(TacGiaRequest request);
    TacGia updateTacGia(Long id, TacGiaRequest request);
    void deleteTacGia(Long id);
}
