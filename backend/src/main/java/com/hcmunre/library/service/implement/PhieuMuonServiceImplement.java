package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.GiaHanRequest;
import com.hcmunre.library.dto.request.MuonSachRequest;
import com.hcmunre.library.dto.request.TraCuonSachRequest;
import com.hcmunre.library.dto.request.TraToanBoRequest;
import com.hcmunre.library.dto.response.GiaHanResponse;
import com.hcmunre.library.dto.response.PhieuMuonResponse;
import com.hcmunre.library.entity.*;
import com.hcmunre.library.enums.TrangThaiChiTietPhieuMuon;
import com.hcmunre.library.enums.TrangThaiCuonSach;
import com.hcmunre.library.enums.TrangThaiDatCho;
import com.hcmunre.library.enums.TrangThaiPhieuMuon;
import com.hcmunre.library.enums.LoaiHinhAnh;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.ChiTietPhieuMuonRepository;
import com.hcmunre.library.repository.DatChoRepository;
import com.hcmunre.library.repository.LichSuGiaHanRepository;
import com.hcmunre.library.repository.PhieuMuonRepository;
import com.hcmunre.library.service.NguoiDungService;
import com.hcmunre.library.service.PhieuMuonService;
import com.hcmunre.library.service.PhieuPhatService;
import com.hcmunre.library.service.CuonSachService;
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
    private static final int SO_LAN_GIA_HAN_TOI_DA = 2;
    private static final int SO_NGAY_GIA_HAN = 7;

    private final PhieuMuonRepository phieuMuonRepository;
    private final ChiTietPhieuMuonRepository chiTietPhieuMuonRepository;
    private final LichSuGiaHanRepository lichSuGiaHanRepository;
    private final NguoiDungService nguoiDungService;
    private final CuonSachService cuonSachService;
    private final PhieuPhatService phieuPhatService;
    private final DatChoRepository datChoRepository;

    @Override
    @Transactional
    public PhieuMuonResponse createPhieuMuon(MuonSachRequest request) {
        NguoiDung nguoiDung = nguoiDungService.getNguoiDungActive(request.getMaNguoiDung());

        if (phieuPhatService.hasPhieuPhatUnpaid(nguoiDung.getMaNguoiDung())) {
            throw new LibraryException(ErrorCode.NGUOI_DUNG_CON_NO_PHAT);
        }

        if(checkCuonSachQuaHan(nguoiDung.getMaNguoiDung())){
            throw new LibraryException(ErrorCode.SACH_DANG_QUA_HAN);
        }

        LocalDateTime now = LocalDateTime.now();
        PhieuMuon phieuMuon = PhieuMuon.builder()
                .ngayMuon(now)
                .trangThaiPhieu(TrangThaiPhieuMuon.CHUA_HOAN_TAT)
                .nguoiDung(nguoiDung)
                .build();

        List<ChiTietPhieuMuon> danhSachChiTiet = new ArrayList<>();
        LocalDateTime hanTra = now.plusDays(SO_NGAY_MUON_MAC_DINH);

        int SO_SACH_MUON_TOI_DA = 5;
        int soSachDangGiu = chiTietPhieuMuonRepository.countByPhieuMuon_NguoiDung_MaNguoiDungAndTrangThaiChiTietPhieuMuonIn(
                nguoiDung.getMaNguoiDung(), List.of(TrangThaiChiTietPhieuMuon.DANG_MUON, TrangThaiChiTietPhieuMuon.QUA_HAN));

        if(soSachDangGiu + request.getDanhSachMaCuonSach().size() > SO_SACH_MUON_TOI_DA){
            throw new LibraryException(ErrorCode.VUOT_QUA_GIOI_HAN_MUON);
        }

        for (Long maCuonSach : request.getDanhSachMaCuonSach()) {
            CuonSach cuonSach = cuonSachService.getCuonSachAvailable(maCuonSach);

            List<DatCho> danhSachDatCho = datChoRepository.findByNguoiDung_MaNguoiDungAndSach_MaSachAndTrangThai(
                    request.getMaNguoiDung(), cuonSach.getSach().getMaSach(), TrangThaiDatCho.DANG_CHO);

            if(!danhSachDatCho.isEmpty()){
                DatCho datCho = danhSachDatCho.get(0);
                datCho.setTrangThai(TrangThaiDatCho.DA_NHAN_SACH);
                datChoRepository.save(datCho);
            }

            ChiTietPhieuMuon chiTietPhieuMuon = ChiTietPhieuMuon.builder()
                    .phieuMuon(phieuMuon)
                    .cuonSach(cuonSach)
                    .tinhTrangLucMuon(cuonSach.getTinhTrangVatLy())
                    .donGiaPhatApDung(cuonSach.getSach().getDonGiaPhatTheoNgay())
                    .trangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon.DANG_MUON)
                    .hanTraBanDau(hanTra)
                    .hanTraHienTai(hanTra)
                    .soLanGiaHan(0)
                    .build();

            danhSachChiTiet.add(chiTietPhieuMuon);
            cuonSachService.updateTrangThaiCuonSach(maCuonSach, TrangThaiCuonSach.DANG_MUON);
        }

        phieuMuon.setDanhSachChiTietPhieuMuon(danhSachChiTiet);
        PhieuMuon saved = phieuMuonRepository.save(phieuMuon);

        return toPhieuMuonResponse(saved);
    }

    @Override
    @Transactional
    public void cancelPhieuMuon(UUID maPhieuMuon) {
        PhieuMuon phieuMuon = phieuMuonRepository.findById(maPhieuMuon).orElseThrow(
                () -> new LibraryException(ErrorCode.PHIEU_MUON_KHONG_TON_TAI));

        if (phieuMuon.getTrangThaiPhieu() == TrangThaiPhieuMuon.DA_HOAN_TAT) {
            throw new LibraryException(ErrorCode.PHIEU_MUON_DA_HOAN_TAT);
        }
        if (phieuMuon.getTrangThaiPhieu() == TrangThaiPhieuMuon.DA_HUY) {
            throw new LibraryException(ErrorCode.PHIEU_MUON_DA_HUY);
        }

        boolean hasReturnBooks = phieuMuon.getDanhSachChiTietPhieuMuon().stream()
                .anyMatch(ct -> ct.getTrangThaiChiTietPhieuMuon() != TrangThaiChiTietPhieuMuon.DANG_MUON
                    && ct.getTrangThaiChiTietPhieuMuon() != TrangThaiChiTietPhieuMuon.QUA_HAN);
        if(hasReturnBooks){
            throw new LibraryException(ErrorCode.KHONG_THE_HUY_PHIEU_DANG_SU_DUNG);
        }

        for (ChiTietPhieuMuon chiTietPhieuMuon : phieuMuon.getDanhSachChiTietPhieuMuon()) {
             chiTietPhieuMuon.setTrangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon.DA_HUY);
             cuonSachService.updateTrangThaiCuonSach(chiTietPhieuMuon.getCuonSach().getMaCuonSach(), TrangThaiCuonSach.SAN_SANG);
        }
        phieuMuon.setTrangThaiPhieu(TrangThaiPhieuMuon.DA_HUY);
        phieuMuonRepository.save(phieuMuon);
    }

    @Override
    @Transactional
    public PhieuMuonResponse.ChitietResponse returnCuonSach(TraCuonSachRequest request) {
        ChiTietPhieuMuon chiTietPhieuMuon = chiTietPhieuMuonRepository.findById(request.getMaChiTietPhieuMuon())
                .orElseThrow(
                        () -> new LibraryException(ErrorCode.PHIEU_MUON_KHONG_TON_TAI));

        if (chiTietPhieuMuon.getTrangThaiChiTietPhieuMuon() == TrangThaiChiTietPhieuMuon.DA_TRA
                || chiTietPhieuMuon.getTrangThaiChiTietPhieuMuon() == TrangThaiChiTietPhieuMuon.DA_TRA_TRE) {
            throw new LibraryException(ErrorCode.CHI_TIET_DA_TRA);
        }

        LocalDateTime now = LocalDateTime.now();
        chiTietPhieuMuon.setNgayTraThucTe(now);
        chiTietPhieuMuon.setTinhTrangLucTra(request.getTinhTrangLucTra());

        cuonSachService.updateTrangThaiCuonSach(chiTietPhieuMuon.getCuonSach().getMaCuonSach(),
                TrangThaiCuonSach.SAN_SANG);

        if (now.isAfter(chiTietPhieuMuon.getHanTraHienTai())) {
            chiTietPhieuMuon.setTrangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon.DA_TRA_TRE);
            Long soNgayTre = ChronoUnit.DAYS.between((chiTietPhieuMuon.getHanTraHienTai()), now);
            double tienPhat = soNgayTre * chiTietPhieuMuon.getDonGiaPhatApDung();
            phieuPhatService.createPhieuPhat(chiTietPhieuMuon.getMaChiTietPhieuMuon(), tienPhat,
                    "Trả trễ " + soNgayTre + " ngày");
        } else {
            chiTietPhieuMuon.setTrangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon.DA_TRA);
        }

        if (request.getTinhTrangLucTra().ordinal() > chiTietPhieuMuon.getTinhTrangLucMuon().ordinal()) {
            phieuPhatService.createPhieuPhat(chiTietPhieuMuon.getMaChiTietPhieuMuon(), 50000.0,
                    "Sách bị hư hỏng");
        }

        chiTietPhieuMuonRepository.save(chiTietPhieuMuon);

        PhieuMuon phieuMuon = chiTietPhieuMuon.getPhieuMuon();
        boolean tatCaDaTra = phieuMuon.getDanhSachChiTietPhieuMuon().stream()
                .noneMatch(ct -> ct.getTrangThaiChiTietPhieuMuon() == TrangThaiChiTietPhieuMuon.DANG_MUON
                    || ct.getTrangThaiChiTietPhieuMuon() == TrangThaiChiTietPhieuMuon.QUA_HAN);

        if (tatCaDaTra) {
            phieuMuon.setTrangThaiPhieu(TrangThaiPhieuMuon.DA_HOAN_TAT);
            phieuMuonRepository.save(phieuMuon);
        }

        return toChiTietResponse(chiTietPhieuMuon);
    }

    @Override
    @Transactional
    public GiaHanResponse createGiaHan(GiaHanRequest request) {
        ChiTietPhieuMuon chiTietPhieuMuon = chiTietPhieuMuonRepository.findById(request.getMaChiTietPhieuMuon())
                .orElseThrow(
                        () -> new LibraryException(ErrorCode.PHIEU_MUON_KHONG_TON_TAI));

        if (chiTietPhieuMuon.getNgayTraThucTe() != null) {
            throw new LibraryException(ErrorCode.CHI_TIET_DA_TRA);
        }

        if (chiTietPhieuMuon.getSoLanGiaHan() >= SO_LAN_GIA_HAN_TOI_DA) {
            throw new LibraryException(ErrorCode.KHONG_THE_GIA_HAN_QUA_HAN);
        }

        LocalDateTime hanTraCu = chiTietPhieuMuon.getHanTraHienTai();
        LocalDateTime hanTraMoi = hanTraCu.plusDays(SO_NGAY_GIA_HAN);
        
        NguoiDung nguoiThucHien = nguoiDungService.getNguoiDungActive(request.getMaNguoiThucHien());

        LichSuGiaHan lichSuGiaHan = LichSuGiaHan.builder()
                .chiTietPhieuMuon(chiTietPhieuMuon)
                .hanTraCu(hanTraCu)
                .hanTraMoi(hanTraMoi)
                .lyDo(request.getLyDo())
                .nguoiThucHien(nguoiThucHien)
                .build();

        lichSuGiaHanRepository.save(lichSuGiaHan);

        chiTietPhieuMuon.setHanTraHienTai(hanTraMoi);
        chiTietPhieuMuon.setSoLanGiaHan(chiTietPhieuMuon.getSoLanGiaHan() + 1);
        chiTietPhieuMuonRepository.save(chiTietPhieuMuon);
        return toGiaHanResponse(lichSuGiaHan);
    }

    @Override
    public List<GiaHanResponse> getLichSuGiaHanByChiTiet(UUID maChiTietPhieuMuon) {
        return lichSuGiaHanRepository.findByChiTietPhieuMuon_MaChiTietPhieuMuonOrderByNgayTaoDesc(maChiTietPhieuMuon)
                .stream().map(this::toGiaHanResponse).collect(Collectors.toList());
    }

    @Override
    public List<PhieuMuonResponse> getAllPhieuMuon() {
        return phieuMuonRepository.findAll().stream().map(this::toPhieuMuonResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PhieuMuonResponse getPhieuMuonById(UUID maPhieuMuon) {
        return phieuMuonRepository.findById(maPhieuMuon)
                .map(this::toPhieuMuonResponse)
                .orElseThrow(() -> new LibraryException(ErrorCode.PHIEU_MUON_KHONG_TON_TAI));
    }

    @Override
    public List<PhieuMuonResponse> getPhieuMuonByNguoiDung(UUID maNguoiDung) {
        return phieuMuonRepository.findByNguoiDung_MaNguoiDungOrderByNgayMuonDesc(maNguoiDung)
                .stream().map(this::toPhieuMuonResponse)
                .collect(Collectors.toList());
    }

    public boolean checkCuonSachQuaHan(UUID maNguoiDung){
        return chiTietPhieuMuonRepository.existsByPhieuMuon_NguoiDung_MaNguoiDungAndTrangThaiChiTietPhieuMuon(maNguoiDung, TrangThaiChiTietPhieuMuon.QUA_HAN);
    }

    @Override
    @Transactional
    public void reportMatSach(UUID maChiTietPhieuMuon){
        ChiTietPhieuMuon chiTietPhieuMuon = chiTietPhieuMuonRepository.findById(maChiTietPhieuMuon)
                .orElseThrow(() -> new LibraryException(ErrorCode.CHI_TIET_PHIEU_MUON_KHONG_TON_TAI));

        if(chiTietPhieuMuon.getTrangThaiChiTietPhieuMuon() != TrangThaiChiTietPhieuMuon.DANG_MUON
            && chiTietPhieuMuon.getTrangThaiChiTietPhieuMuon() != TrangThaiChiTietPhieuMuon.QUA_HAN){
            throw new LibraryException(ErrorCode.KHONG_THE_BAO_MAT_SACH_DA_TRA);
        }

        chiTietPhieuMuon.setTrangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon.MAT_SACH);
        chiTietPhieuMuon.setNgayTraThucTe(LocalDateTime.now());
        chiTietPhieuMuonRepository.save(chiTietPhieuMuon);

        cuonSachService.updateTrangThaiCuonSach(chiTietPhieuMuon.getCuonSach().getMaCuonSach(), TrangThaiCuonSach.DA_MAT);

        double tienPhat = chiTietPhieuMuon.getCuonSach().getSach().getGiaTien() * 1.5;
        phieuPhatService.createPhieuPhat(chiTietPhieuMuon.getMaChiTietPhieuMuon(), tienPhat, "Đền bù làm mất sách");

        PhieuMuon phieuMuon = chiTietPhieuMuon.getPhieuMuon();
        boolean tatCaDaXong = phieuMuon.getDanhSachChiTietPhieuMuon().stream().noneMatch(
                ct -> ct.getTrangThaiChiTietPhieuMuon() == TrangThaiChiTietPhieuMuon.DANG_MUON ||
                        ct.getTrangThaiChiTietPhieuMuon() == TrangThaiChiTietPhieuMuon.QUA_HAN
        );

        if(tatCaDaXong){
            phieuMuon.setTrangThaiPhieu(TrangThaiPhieuMuon.DA_HOAN_TAT);
            phieuMuonRepository.save(phieuMuon);
        }
    }

    @Override
    @Transactional
    public PhieuMuonResponse returnToanBoPhieuMuon(TraToanBoRequest request){
        PhieuMuon phieuMuon = phieuMuonRepository.findById(request.getMaPhieuMuon())
                .orElseThrow(() -> new LibraryException(ErrorCode.PHIEU_MUON_KHONG_TON_TAI));

        List<ChiTietPhieuMuon> chiTietPhieuMuonList = phieuMuon.getDanhSachChiTietPhieuMuon().stream()
                .filter(ct -> ct.getTrangThaiChiTietPhieuMuon() == TrangThaiChiTietPhieuMuon.DANG_MUON
                    || ct.getTrangThaiChiTietPhieuMuon() == TrangThaiChiTietPhieuMuon.QUA_HAN).toList();

        for (ChiTietPhieuMuon ct : chiTietPhieuMuonList){
            TraCuonSachRequest traCuonSachRequest = new TraCuonSachRequest();
            traCuonSachRequest.setMaChiTietPhieuMuon(ct.getMaChiTietPhieuMuon());
            traCuonSachRequest.setTinhTrangLucTra(request.getTinhTrangLucTra() != null ? request.getTinhTrangLucTra() : ct.getTinhTrangLucMuon());
            returnCuonSach(traCuonSachRequest);
        }

        return getPhieuMuonById(request.getMaPhieuMuon());
    }

    private PhieuMuonResponse toPhieuMuonResponse(PhieuMuon phieuMuon) {
        List<PhieuMuonResponse.ChitietResponse> chitietList = phieuMuon.getDanhSachChiTietPhieuMuon().stream()
                .map(ct -> PhieuMuonResponse.ChitietResponse.builder()
                        .maChiTietPhieuMuon(ct.getMaChiTietPhieuMuon())
                        .maCuonSach(ct.getCuonSach().getMaCuonSach())
                        .maVach(ct.getCuonSach().getMaVach())
                        .hanTraBanDau(ct.getHanTraBanDau())
                        .hanTraHienTai(ct.getHanTraHienTai())
                        .tinhTrangLucMuon(ct.getTinhTrangLucMuon())
                        .tinhTrangLucTra(ct.getTinhTrangLucTra())
                        .tenSach(ct.getCuonSach().getSach().getTenSach())
                        .anhBiaUrl(ct.getCuonSach().getSach().getDanhSachHinhAnh() != null ? 
                                ct.getCuonSach().getSach().getDanhSachHinhAnh().stream()
                                .filter(img -> img.getLoaiHinhAnh() == LoaiHinhAnh.BIA_TRUOC)
                                .findFirst()
                                .map(HinhAnhSach::getDuongDan)
                                .orElse(null) : null)
                        .soLanGiaHan(ct.getSoLanGiaHan())
                        .donGiaPhatApDung(ct.getDonGiaPhatApDung())
                        .trangThaiChiTietPhieuMuon(ct.getTrangThaiChiTietPhieuMuon())
                        .build())
                .collect(Collectors.toList());

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
                .tenSach(ct.getCuonSach().getSach().getTenSach())
                .anhBiaUrl(ct.getCuonSach().getSach().getDanhSachHinhAnh() != null ? 
                        ct.getCuonSach().getSach().getDanhSachHinhAnh().stream()
                        .filter(img -> img.getLoaiHinhAnh() == LoaiHinhAnh.BIA_TRUOC)
                        .findFirst()
                        .map(HinhAnhSach::getDuongDan)
                        .orElse(null) : null)
                .soLanGiaHan(ct.getSoLanGiaHan())
                .donGiaPhatApDung(ct.getDonGiaPhatApDung())
                .trangThaiChiTietPhieuMuon(ct.getTrangThaiChiTietPhieuMuon())
                .build();
    }

    private GiaHanResponse toGiaHanResponse(LichSuGiaHan ls) {
        return GiaHanResponse.builder()
                .maLichSuGiaHan(ls.getMaLichSuGiaHan())
                .maChiTietPhieuMuon(ls.getChiTietPhieuMuon().getMaChiTietPhieuMuon())
                .hanTraCu(ls.getHanTraCu())
                .hanTraMoi(ls.getHanTraMoi())
                .lyDo(ls.getLyDo())
                .tenNguoiThucHien(ls.getNguoiThucHien() != null ? ls.getNguoiThucHien().getHoDem() + " " + ls.getNguoiThucHien().getTen() : null)
                .build();
    }

}
