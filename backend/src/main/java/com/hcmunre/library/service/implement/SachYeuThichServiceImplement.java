package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.response.SachYeuThichResponse;
import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.entity.Sach;
import com.hcmunre.library.entity.SachYeuThich;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.repository.SachRepository;
import com.hcmunre.library.repository.SachYeuThichRepository;
import com.hcmunre.library.service.NguoiDungService;
import com.hcmunre.library.service.SachYeuThichService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SachYeuThichServiceImplement implements SachYeuThichService {
    private final SachYeuThichRepository sachYeuThichRepository;
    private final NguoiDungService nguoiDungService;
    private final SachRepository sachRepository;

    @Override
    @Transactional
    public void themVaoDanhSachYeuThich(UUID maNguoiDung, Long maSach) {
        NguoiDung nguoiDung = nguoiDungService.getNguoiDungActive(maNguoiDung);

        if(sachYeuThichRepository.existsByNguoiDung_MaNguoiDungAndSach_MaSach(maNguoiDung, maSach)){
            throw new LibraryException(ErrorCode.SACH_DA_CO_TRONG_YEU_THICH);
        }

        Sach sach = sachRepository.findById(maSach).orElseThrow(() -> new LibraryException(ErrorCode.SACH_KHONG_TON_TAI));

        SachYeuThich sachYeuThich = SachYeuThich.builder()
                .nguoiDung(nguoiDung)
                .sach(sach)
                .build();

        sachYeuThichRepository.save(sachYeuThich);
    }

    @Override
    @Transactional
    public void xoaKhoiDanhSachYeuThich(UUID maNguoiDung, Long maSach){
        if(!sachYeuThichRepository.existsByNguoiDung_MaNguoiDungAndSach_MaSach(maNguoiDung, maSach)){
            throw new LibraryException(ErrorCode.SACH_YEU_THICH_KHONG_TON_TAI);
        }

        sachYeuThichRepository.deleteByNguoiDung_MaNguoiDungAndSach_MaSach(maNguoiDung, maSach);
    }

    @Override
    public List<SachYeuThichResponse> getDanhSachYeuThich(UUID maNguoiDung) {

        return sachYeuThichRepository.findByNguoiDung_MaNguoiDung(maNguoiDung)
                .stream().map(item -> SachYeuThichResponse.builder()
                        .id(item.getSach().getMaSach())
                        .maSach(item.getSach().getMaSach())
                        .tenSach(item.getSach().getTenSach())
                        .maIsbn(item.getSach().getMaIsbn())
                        .build()).collect(Collectors.toList());
    }
}
