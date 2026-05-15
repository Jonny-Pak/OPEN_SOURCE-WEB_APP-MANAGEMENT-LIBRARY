package com.hcmunre.library.service;

import com.hcmunre.library.dto.response.ThongBaoResponse;
import com.hcmunre.library.enums.LoaiThongBao;

import java.util.List;
import java.util.UUID;

public interface ThongBaoService {
    void taoThongBao(UUID maNguoiDung, String tieuDe, String noiDung, LoaiThongBao loaiThongBao);
    List<ThongBaoResponse> layDanhSachThongBao(UUID maNguoiDung);
    void danhDauDaDoc(UUID maThongBao);
    void danhDauTatCaDaDoc(UUID maNguoiDung);
    long demSoThongBaoChuaDoc(UUID maNguoiDung);
}
