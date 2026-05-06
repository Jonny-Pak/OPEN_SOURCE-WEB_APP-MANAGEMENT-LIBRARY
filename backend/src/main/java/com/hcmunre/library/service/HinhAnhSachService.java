package com.hcmunre.library.service;

import com.hcmunre.library.entity.HinhAnhSach;
import java.util.List;

public interface HinhAnhSachService {
    // Queries
    List<HinhAnhSach> getAllHinhAnh();
    List<HinhAnhSach> getHinhAnhBySach(Long maSach);
    HinhAnhSach getHinhAnhById(Long id);

    // Commands
    HinhAnhSach createHinhAnh(HinhAnhSach hinhAnhSach);
    void deleteHinhAnh(Long id);
}
