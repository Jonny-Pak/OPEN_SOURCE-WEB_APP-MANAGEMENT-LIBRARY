package com.hcmunre.library.service;

import com.hcmunre.library.dto.response.SachYeuThichResponse;

import java.util.List;
import java.util.UUID;

public interface SachYeuThichService{
    void themVaoDanhSachYeuThich(UUID maNguoiDung, Long maSach);
    void xoaKhoiDanhSachYeuThich(UUID maNguoiDung, Long maSach);
    List<SachYeuThichResponse> getDanhSachYeuThich(UUID maNguoiDung);
}
