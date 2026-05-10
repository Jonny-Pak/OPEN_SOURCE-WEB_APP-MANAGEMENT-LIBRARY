package com.hcmunre.library.service;

import com.hcmunre.library.dto.request.HinhAnhSachRequest;
import com.hcmunre.library.dto.response.HinhAnhSachResponse;
import java.util.List;

public interface HinhAnhSachService {
    // Queries
    List<HinhAnhSachResponse> getAllHinhAnh();
    List<HinhAnhSachResponse> getHinhAnhBySach(Long maSach);
    HinhAnhSachResponse getHinhAnhById(Long id);

    // Commands
    HinhAnhSachResponse createHinhAnh(HinhAnhSachRequest request);
    void deleteHinhAnh(Long id);
}
