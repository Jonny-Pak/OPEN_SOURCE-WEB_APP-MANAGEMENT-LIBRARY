package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.SachRequest;
import com.hcmunre.library.entity.Sach;

import java.util.List;

public interface SachService {
    // Queries
    List<Sach> getAllSach();
    List<Sach> searchSach(String keyword);
    Sach getSachById(Long id);

    // Commands
    Sach createSach(SachRequest request);
    Sach updateSach(Long id, SachRequest request);
    void deleteSach(Long id);
}
