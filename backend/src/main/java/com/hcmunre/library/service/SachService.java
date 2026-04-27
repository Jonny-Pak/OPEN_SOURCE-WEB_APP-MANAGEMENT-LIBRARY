package com.hcmunre.library.service;

import com.hcmunre.library.entity.Sach;

public interface SachService {
    Sach getSachAvailable(Long maSach);

    void updateSoLuongSach(Long maSach, int delta);
}
