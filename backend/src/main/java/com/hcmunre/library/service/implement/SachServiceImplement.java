package com.hcmunre.library.service.implement;

import com.hcmunre.library.entity.CuonSach;
import com.hcmunre.library.enums.TrangThaiCuonSach;
import com.hcmunre.library.service.SachService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SachServiceImplement implements SachService {
    @Override
    public CuonSach getCuonSachAvailable(Long maSach) {
        return null;
    }

    @Override
    public void updateTrangThaiCuonSach(Long maSach, TrangThaiCuonSach trangThai) {

    }

}
