package com.hcmunre.library.service.implement;

import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.entity.NhatKyHoatDong;
import com.hcmunre.library.repository.NguoiDungRepository;
import com.hcmunre.library.repository.NhatKyHoatDongRepository;
import com.hcmunre.library.service.NhatKyHoatDongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NhatKyHoatDongServiceImplement implements NhatKyHoatDongService {
    private final NhatKyHoatDongRepository nhatKyHoatDongRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    @Transactional
    public void ghiLog(UUID maNguoiDung, String hanhDong, String chiTiet) {
        String hoTen = "Khách vãng lai";
        String vaiTro = "KHACH";

        if (maNguoiDung != null) {
            NguoiDung user = nguoiDungRepository.findById(maNguoiDung).orElse(null);
            if (user != null) {
                hoTen = user.getHoTen();
                vaiTro = user.getVaiTro() != null ? user.getVaiTro().name() : "DOC_GIA";
            }
        }

        NhatKyHoatDong log = NhatKyHoatDong.builder()
                .maNguoiDung(maNguoiDung)
                .hoTen(hoTen)
                .vaiTro(vaiTro)
                .hanhDong(hanhDong)
                .chiTiet(chiTiet)
                .build();

        nhatKyHoatDongRepository.save(log);
    }

    @Override
    public Page<NhatKyHoatDong> getLogs(String keyword, String vaiTro, Pageable pageable) {
        String searchKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        String searchRole = (vaiTro == null || vaiTro.trim().isEmpty()) ? null : vaiTro.trim();
        
        return nhatKyHoatDongRepository.searchLogs(searchKeyword, searchRole, pageable);
    }
}
