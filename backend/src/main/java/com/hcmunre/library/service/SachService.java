package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.SachRequest;
import com.hcmunre.library.dto.response.SachResponse;

import java.util.List;

public interface SachService {
    // Queries
    List<SachResponse> getAllSach();
    List<SachResponse> searchSach(String keyword);
    SachResponse getSachById(Long id);

    // Commands
    SachResponse createSach(SachRequest request);
    SachResponse updateSach(Long id, SachRequest request);
    void deleteSach(Long id);
}
