package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.response.ThongBaoResponse;
import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.entity.ThongBao;
import com.hcmunre.library.enums.LoaiThongBao;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.repository.NguoiDungRepository;
import com.hcmunre.library.repository.ThongBaoRepository;
import com.hcmunre.library.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThongBaoServiceImplement implements ThongBaoService {
    private final ThongBaoRepository thongBaoRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public void taoThongBao(UUID maNguoiDung, String tieuDe, String noiDung, LoaiThongBao loaiThongBao) {
        // Dùng repository trực tiếp để không bị lỗi khi tài khoản chưa active (VD: vừa khóa)
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new LibraryException(ErrorCode.NGUOI_DUNG_KHONG_TON_TAI));

        ThongBao thongBao = ThongBao.builder()
                .nguoiDung(nguoiDung)
                .tieuDe(tieuDe)
                .noiDung(noiDung)
                .loaiThongBao(loaiThongBao)
                .daDoc(false)
                .build();

        thongBaoRepository.save(thongBao);
    }

    @Override
    public void taoThongBaoChoAdmin(String tieuDe, String noiDung, LoaiThongBao loaiThongBao) {
        List<com.hcmunre.library.enums.VaiTro> targetRoles = List.of(
            com.hcmunre.library.enums.VaiTro.QUAN_TRI_VIEN,
            com.hcmunre.library.enums.VaiTro.THU_THU
        );
        List<NguoiDung> admins = nguoiDungRepository.findByVaiTroInAndNgayXoaIsNull(targetRoles);
        for (NguoiDung admin : admins) {
            ThongBao thongBao = ThongBao.builder()
                    .nguoiDung(admin)
                    .tieuDe(tieuDe)
                    .noiDung(noiDung)
                    .loaiThongBao(loaiThongBao)
                    .daDoc(false)
                    .build();
            thongBaoRepository.save(thongBao);
        }
    }

    @Override
    public List<ThongBaoResponse> layDanhSachThongBao(UUID maNguoiDung) {
        return thongBaoRepository.findByNguoiDung_MaNguoiDungOrderByNgayTaoDesc(maNguoiDung)
                .stream().map(item -> ThongBaoResponse.builder()
                .maThongBao(item.getMaThongBao())
                .tieuDe(item.getTieuDe())
                .noiDung(item.getNoiDung())
                .loaiThongBao(item.getLoaiThongBao())
                .daDoc(item.isDaDoc())
                .ngayTao(item.getNgayTao())
                .build()).collect(Collectors.toList());
    }

    @Override
    public void danhDauDaDoc(UUID maThongBao) {
        ThongBao thongBao = thongBaoRepository.findById(maThongBao)
                .orElseThrow(() -> new LibraryException(ErrorCode.THONG_BAO_KHONG_TON_TAI));

        if(!thongBao.isDaDoc()){
            thongBao.setDaDoc(true);
            thongBaoRepository.save(thongBao);
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void danhDauTatCaDaDoc(UUID maNguoiDung) {
        List<ThongBao> chuaDocList = thongBaoRepository.findByNguoiDung_MaNguoiDungAndDaDocFalse(maNguoiDung);
        for (ThongBao tb : chuaDocList) {
            tb.setDaDoc(true);
        }
        thongBaoRepository.saveAll(chuaDocList);
    }

    @Override
    public long demSoThongBaoChuaDoc(UUID maNguoiDung) {
        return thongBaoRepository.countByNguoiDung_MaNguoiDungAndDaDocFalse(maNguoiDung);
    }
}
