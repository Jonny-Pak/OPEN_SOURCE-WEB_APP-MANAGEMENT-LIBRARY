package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.TheLoaiRequest;
import com.hcmunre.library.dto.response.TheLoaiResponse;
import java.util.List;

public interface TheLoaiService {
    // Queries
    List<TheLoaiResponse> getAllTheLoai();
    List<TheLoaiResponse> searchTheLoai(String keyword);
    TheLoaiResponse getTheLoaiById(Long id);

    // Commands
    TheLoaiResponse createTheLoai(TheLoaiRequest request);
    TheLoaiResponse updateTheLoai(Long id, TheLoaiRequest request);
    void deleteTheLoai(Long id);
}
