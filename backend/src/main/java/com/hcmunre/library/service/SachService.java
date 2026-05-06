package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.SachRequest;
import com.hcmunre.library.entity.Sach;

import java.util.List;

public interface SachService {
    List<Sach> getAllSach();
    List<Sach> searchSach(String keyword);
    Sach getSachById(Long id);
    Sach createSach(SachRequest request);
    Sach updateSach(Long id, SachRequest request);
    void deleteSach(Long id);
}
