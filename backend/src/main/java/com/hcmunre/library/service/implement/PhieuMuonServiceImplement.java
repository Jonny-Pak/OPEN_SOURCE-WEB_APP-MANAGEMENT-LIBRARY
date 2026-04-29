package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.GiaHanRequest;
import com.hcmunre.library.dto.request.MuonSachRequest;
import com.hcmunre.library.dto.request.TraSachRequest;
import com.hcmunre.library.dto.response.GiaHanResponse;
import com.hcmunre.library.dto.response.PhieuMuonResponse;
import com.hcmunre.library.entity.*;
import com.hcmunre.library.enums.TinhTrangSach;
import com.hcmunre.library.enums.TrangThaiCuonSach;
import com.hcmunre.library.enums.TrangThaiPhieuMuon;
import com.hcmunre.library.exception.BusinessException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.exception.ResourceNotFoundException;
import com.hcmunre.library.repository.ChiTietPhieuMuonRepository;
import com.hcmunre.library.repository.LichSuGiaHanRepository;
import com.hcmunre.library.repository.PhieuMuonRepository;
import com.hcmunre.library.service.NguoiDungService;
import com.hcmunre.library.service.PhieuMuonService;
import com.hcmunre.library.service.PhieuPhatService;
import com.hcmunre.library.service.SachService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhieuMuonServiceImplement implements PhieuMuonService {
    private static final int SO_NGAY_MUON_MAC_DINH = 14;
    private static final int SO_LAN_GIA_HAN_TOI_DA= 2;
    private static final int SO_NGAY_GIA_HAN = 7;


    private final PhieuMuonRepository phieuMuonRepository;
    private final ChiTietPhieuMuonRepository chiTietPhieuMuonRepository;
    private final LichSuGiaHanRepository lichSuGiaHanRepository;
    private final NguoiDungService nguoiDungService;
    private final SachService sachService;
    private final PhieuPhatService phieuPhatService;

    @Override
    @Transactional
    public PhieuMuonResponse createPhieuMuon(MuonSachRequest request) {
        NguoiDung nguoiDung = nguoiDungService.getNguoiDungActive(request.getMaNguoiDung());

        if(phieuPhatService.hasPhieuPhatUnpaid(nguoiDung.getMaNguoiDung())){
            throw new BusinessException(ErrorCode.NGUOI_DUNG_CON_NO_PHAT);
        }

        LocalDateTime now = LocalDateTime.now();
        PhieuMuon phieuMuon = PhieuMuon.builder()
                .ngayMuon(now)
                .trangThaiPhieu(TrangThaiPhieuMuon.DANG_MUON)
                .nguoiDung(nguoiDung)
                .build();

        List<ChiTietPhieuMuon> danhSachChiTiet = new ArrayList<>();
        LocalDateTime hanTra = now.plusDays(SO_NGAY_MUON_MAC_DINH);

        for(Long maCuonSach : request.getDanhSachMaCuonSach()){
            CuonSach cuonSach = sachService.getCuonSachAvailable(maCuonSach);

            ChiTietPhieuMuon chiTietPhieuMuon = ChiTietPhieuMuon.builder()
                    .phieuMuon(phieuMuon)
                    .cuonSach(cuonSach)
                    .tinhTrangLucMuon(cuonSach.getTinhTrangVatLy())
                    .donGiaPhatApDung(cuonSach.getSach().getDonGiaPhatTheoNgay())
                    .hanTraBanDau(hanTra)
                    .hanTraHienTai(hanTra)
                    .soLanGiaHan(0)
                    .build();

            danhSachChiTiet.add(chiTietPhieuMuon);
            sachService.updateTrangThaiCuonSach(maCuonSach, TrangThaiCuonSach.DANG_MUON);
        }

        phieuMuon.setDanhSachChiTietPhieuMuon(danhSachChiTiet);
        PhieuMuon saved = phieuMuonRepository.save(phieuMuon);

        return toPhieuMuonResponse(saved);
    }

    @Override
    @Transactional
    public void cancelPhieuMuon(UUID maPhieuMuon) {
        PhieuMuon phieuMuon = phieuMuonRepository.findById(maPhieuMuon).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.PHIEU_MUON_KHONG_TON_TAI));

        if(phieuMuon.getTrangThaiPhieu() == TrangThaiPhieuMuon.DA_TRA){
            throw new BusinessException(ErrorCode.PHIEU_MUON_DA_TRA);
        }
        if(phieuMuon.getTrangThaiPhieu() == TrangThaiPhieuMuon.DA_HUY){
            throw new BusinessException(ErrorCode.PHIEU_MUON_DA_HUY);
        }

        for(ChiTietPhieuMuon chiTietPhieuMuon : phieuMuon.getDanhSachChiTietPhieuMuon()){
            if(chiTietPhieuMuon.getNgayTraThucTe() == null){
                sachService.updateTrangThaiCuonSach(chiTietPhieuMuon.getCuonSach().getMaCuonSach(), TrangThaiCuonSach.SAN_SANG);
            }
        }
        phieuMuon.setTrangThaiPhieu(TrangThaiPhieuMuon.DA_HUY);
        phieuMuonRepository.save(phieuMuon);
    }

    @Override
    public void returnChiTietPhieuMuon(UUID maChiTietPhieuMuon, TinhTrangSach tinhTrangSach) {

    }

    @Override
    public PhieuMuonResponse.ChitietResponse returnCuonSach(TraSachRequest request) {
        ChiTietPhieuMuon chiTietPhieuMuon = chiTietPhieuMuonRepository.findById(request.getMaChiTietPhieuMuon()).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.PHIEU_MUON_KHONG_TON_TAI));

        if (chiTietPhieuMuon.getNgayTraThucTe() != null){
            throw new BusinessException(ErrorCode.CHI_TIET_DA_TRA);
        }

        LocalDateTime now = LocalDateTime.now();
        chiTietPhieuMuon.setNgayTraThucTe(now);
        chiTietPhieuMuon.setTinhTrangLucTra(request.getTinhTrangLucTra());

        sachService.updateTrangThaiCuonSach(chiTietPhieuMuon.getCuonSach().getMaCuonSach(), TrangThaiCuonSach.SAN_SANG);

        if(now.isAfter(chiTietPhieuMuon.getHanTraHienTai())){
            Long soNgayTre = ChronoUnit.DAYS.between((chiTietPhieuMuon.getHanTraHienTai()), now);
            double tienPhat = soNgayTre * chiTietPhieuMuon.getDonGiaPhatApDung();
            phieuPhatService.createPhieuPhat(chiTietPhieuMuon.getMaChiTietPhieuMuon(),tienPhat, "Trả trễ " + soNgayTre + " ngày");
        }

        if(request.getTinhTrangLucTra().ordinal() > chiTietPhieuMuon.getTinhTrangLucMuon().ordinal()){
            phieuPhatService.createPhieuPhat(chiTietPhieuMuon.getMaChiTietPhieuMuon(), 50000.0, "Sách bị hư hỏng " + request.getTinhTrangLucTra());
        }

        chiTietPhieuMuonRepository.save(chiTietPhieuMuon);

        PhieuMuon phieuMuon = chiTietPhieuMuon.getPhieuMuon();
        boolean tatCaDaTra = phieuMuon.getDanhSachChiTietPhieuMuon().stream()
                .allMatch(ct -> ct.getNgayTraThucTe() != null);
        if(tatCaDaTra){
            phieuMuon.setTrangThaiPhieu(TrangThaiPhieuMuon.DA_TRA);
            phieuMuonRepository.save(phieuMuon);
        }


        return toChiTietResponse(chiTietPhieuMuon);
    }

    @Override
    public void returnPhieuMuon(UUID maPhieuMuon, TinhTrangSach tinhTrangChung) {

    }

    @Override
    public GiaHanResponse createGiaHan(GiaHanRequest request){
        ChiTietPhieuMuon chiTietPhieuMuon = chiTietPhieuMuonRepository.findById(request.getMaChiTietPhieuMuon()).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.PHIEU_MUON_KHONG_TON_TAI));

        if (chiTietPhieuMuon.getNgayTraThucTe() != null){
            throw new BusinessException(ErrorCode.CHI_TIET_DA_TRA);
        }

        if(chiTietPhieuMuon.getSoLanGiaHan() >= SO_LAN_GIA_HAN_TOI_DA){
            throw new BusinessException(ErrorCode.KHONG_THE_GIA_HAN_QUA_HAN);
        }

        LocalDateTime hanTraCu = chiTietPhieuMuon.getHanTraHienTai();
        LocalDateTime hanTraMoi = hanTraCu.plusDays(SO_NGAY_GIA_HAN);

        LichSuGiaHan lichSuGiaHan = LichSuGiaHan.builder()
                .chiTietPhieuMuon(chiTietPhieuMuon)
                .hanTraCu(hanTraCu)
                .hanTraMoi(hanTraMoi)
                .lyDo(request.getLyDo())
                .build();

        lichSuGiaHanRepository.save(lichSuGiaHan);

        chiTietPhieuMuon.setHanTraHienTai(hanTraMoi);
        chiTietPhieuMuon.setSoLanGiaHan(chiTietPhieuMuon.getSoLanGiaHan() + 1);
        chiTietPhieuMuonRepository.save(chiTietPhieuMuon);
        return toGiaHanResponse(lichSuGiaHan);
    }

    @Override
    public List<GiaHanResponse> getLichSuGiaHanByChiTiet(UUID maChiTietPhieuMuon){
        return lichSuGiaHanRepository.findByChiTietPhieuMuon_MaChiTietPhieuMuonOrderByNgayTaoDesc(maChiTietPhieuMuon)
                .stream().map(this::toGiaHanResponse).collect(Collectors.toList());
    }







    private PhieuMuonResponse toPhieuMuonResponse(PhieuMuon phieuMuon){
        List<PhieuMuonResponse.ChitietResponse> chitietList = phieuMuon.getDanhSachChiTietPhieuMuon().stream()
                .map(ct -> PhieuMuonResponse.ChitietResponse.builder()
                        .maChiTietPhieuMuon(ct.getMaChiTietPhieuMuon())
                        .maCuonSach(ct.getCuonSach().getMaCuonSach())
                        .maVach(ct.getCuonSach().getMaVach())
                        .hanTraBanDau(ct.getHanTraBanDau())
                        .hanTraHienTai(ct.getHanTraHienTai())
                        .tinhTrangLucMuon(ct.getTinhTrangLucMuon())
                        .tinhTrangLucTra(ct.getTinhTrangLucTra())
                        .soLanGiaHan(ct.getSoLanGiaHan())
                        .donGiaPhatApDung(ct.getDonGiaPhatApDung())
                        .build()).collect(Collectors.toList());

        NguoiDung nguoiDung = phieuMuon.getNguoiDung();
        return PhieuMuonResponse.builder()
                .maPhieuMuon(phieuMuon.getMaPhieuMuon())
                .maNguoiDung(nguoiDung.getMaNguoiDung())
                .tenDocGia(nguoiDung.getHoDem() + " " + nguoiDung.getTen())
                .ngayMuon(phieuMuon.getNgayMuon())
                .trangThaiPhieu(phieuMuon.getTrangThaiPhieu())
                .danhSachChiTiet(chitietList)
                .build();
    }

    private PhieuMuonResponse.ChitietResponse toChiTietResponse(ChiTietPhieuMuon ct) {
        return PhieuMuonResponse.ChitietResponse.builder()
                .maChiTietPhieuMuon(ct.getMaChiTietPhieuMuon())
                .maCuonSach(ct.getCuonSach().getMaCuonSach())
                .maVach(ct.getCuonSach().getMaVach())
                .hanTraBanDau(ct.getHanTraBanDau())
                .hanTraHienTai(ct.getHanTraHienTai())
                .ngayTraThucTe(ct.getNgayTraThucTe())
                .tinhTrangLucMuon(ct.getTinhTrangLucMuon())
                .tinhTrangLucTra(ct.getTinhTrangLucTra())
                .soLanGiaHan(ct.getSoLanGiaHan())
                .donGiaPhatApDung(ct.getDonGiaPhatApDung())
                .build();
    }
    private GiaHanResponse toGiaHanResponse(LichSuGiaHan ls) {
        return GiaHanResponse.builder()
                .maLichSuGiaHan(ls.getMaLichSuGiaHan())
                .maChiTietPhieuMuon(ls.getChiTietPhieuMuon().getMaChiTietPhieuMuon())
                .hanTraCu(ls.getHanTraCu())
                .hanTraMoi(ls.getHanTraMoi())
                .lyDo(ls.getLyDo())
                .tenNguoiThucHien(ls.getNguoiThucHien().getHoDem() + " " + ls.getNguoiThucHien().getTen())
                .build();
    }





}
