package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.SachRequest;
import com.hcmunre.library.dto.response.SachResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SachService {
    // Queries
    Page<SachResponse> getAllSach(Pageable pageable);
    Page<SachResponse> searchAndFilterSach(String keyword, Long maTheLoai, Long maTacGia, Long maNhaXuatBan, Pageable pageable);
    SachResponse getSachById(Long id);

    // Commands
    SachResponse createSach(SachRequest request);
    SachResponse updateSach(Long id, SachRequest request);
    void deleteSach(Long id);
}
