package com.hcmunre.library.service;

import com.hcmunre.library.entity.Sach;

import java.util.UUID;

public interface SachService {
    Sach getSachAvailable(Long maSach);

    void updateSoLuongSach(Long maSach, int delta);
}
