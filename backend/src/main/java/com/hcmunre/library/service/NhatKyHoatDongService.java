package com.hcmunre.library.service;

import com.hcmunre.library.entity.NhatKyHoatDong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NhatKyHoatDongService {
    void ghiLog(UUID maNguoiDung, String hanhDong, String chiTiet);
    Page<NhatKyHoatDong> getLogs(String keyword, String vaiTro, Pageable pageable);
}
