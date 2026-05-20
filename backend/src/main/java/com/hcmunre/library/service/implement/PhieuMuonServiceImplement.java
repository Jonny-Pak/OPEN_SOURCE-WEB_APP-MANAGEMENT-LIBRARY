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
import com.hcmunre.library.enums.LoaiHinhAnh;
import com.hcmunre.library.enums.TrangThaiGiaHan;
import com.hcmunre.library.enums.TrangThaiPhieuMuon;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.ChiTietPhieuMuonRepository;
import com.hcmunre.library.repository.DatChoRepository;
import com.hcmunre.library.repository.LichSuGiaHanRepository;
import com.hcmunre.library.repository.PhieuMuonRepository;
import com.hcmunre.library.service.*;
import com.hcmunre.library.enums.LoaiThongBao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    private static final int SO_SACH_MUON_TOI_DA = 5;

    private final PhieuMuonRepository phieuMuonRepository;
    private final ChiTietPhieuMuonRepository chiTietPhieuMuonRepository;
    private final LichSuGiaHanRepository lichSuGiaHanRepository;
    private final NguoiDungService nguoiDungService;
    private final CuonSachService cuonSachService;
    private final PhieuPhatService phieuPhatService;
    private final DatChoRepository datChoRepository;
    private final EmailOutboxService emailOutboxService;
    private final ThongBaoService thongBaoService;
    private final com.hcmunre.library.service.NhatKyHoatDongService nhatKyHoatDongService;

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

        int soSachDangGiu = chiTietPhieuMuonRepository.countByPhieuMuon_NguoiDung_MaNguoiDungAndTrangThaiChiTietPhieuMuonIn(
                nguoiDung.getMaNguoiDung(), List.of(TrangThaiChiTietPhieuMuon.DANG_MUON, TrangThaiChiTietPhieuMuon.QUA_HAN));

        if(soSachDangGiu + request.getDanhSachMaVach().size() > SO_SACH_MUON_TOI_DA){
            throw new LibraryException(ErrorCode.VUOT_QUA_GIOI_HAN_MUON);
        }

        for (String maVach : request.getDanhSachMaVach()) {
            // Delegates status check to shared helper — avoids duplicating SAN_SANG check inline
            CuonSach cuonSach = getCuonSachAvailableByMaVach(maVach);

            List<DatCho> danhSachDatCho = datChoRepository.findByNguoiDung_MaNguoiDungAndSach_MaSachAndTrangThai(
                    request.getMaNguoiDung(), cuonSach.getSach().getMaSach(), TrangThaiDatCho.DANG_CHO);

            if(!danhSachDatCho.isEmpty()){
                DatCho datCho = danhSachDatCho.get(0);
                datCho.setTrangThai(TrangThaiDatCho.DA_NHAN_SACH);
                datChoRepository.save(datCho);
            }

            Double donGiaPhat = cuonSach.getSach().getDonGiaPhatTheoNgay() != null
                    ? cuonSach.getSach().getDonGiaPhatTheoNgay() : 0.0;

            ChiTietPhieuMuon chiTietPhieuMuon = ChiTietPhieuMuon.builder()
                    .phieuMuon(phieuMuon)
                    .cuonSach(cuonSach)
                    .tinhTrangLucMuon(cuonSach.getTinhTrangVatLy())
                    .donGiaPhatApDung(donGiaPhat)
                    .trangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon.DANG_MUON)
                    .hanTraBanDau(hanTra)
                    .hanTraHienTai(hanTra)
                    .soLanGiaHan(0)
                    .build();

            danhSachChiTiet.add(chiTietPhieuMuon);
            cuonSachService.updateTrangThaiCuonSach(cuonSach.getMaCuonSach(), TrangThaiCuonSach.DANG_MUON);
        }

        phieuMuon.setDanhSachChiTietPhieuMuon(danhSachChiTiet);
        PhieuMuon saved = phieuMuonRepository.save(phieuMuon);

        try {
            nhatKyHoatDongService.ghiLog(
                getMaNguoiDungHienTai(),
                "Mượn sách",
                "Tạo phiếu mượn sách thành công cho độc giả " + nguoiDung.getHoTen() + " (" + nguoiDung.getEmail() + ") - Số lượng cuốn sách: " + request.getDanhSachMaVach().size()
            );
        } catch (Exception e) {}

        emailOutboxService.lenLichGuiEmail(
                nguoiDung.getEmail(),
                "[Thư Viện] Xác nhận mượn sách thành công",
                "Chào " + nguoiDung.getHoTen() + ",<br><br>" +
                        "Bạn vừa mượn thành công " + request.getDanhSachMaVach().size() + " cuốn sách. " +
                        "Vui lòng trả đúng hạn để không bị phạt nhé.<br><br>" +
                        "Trân trọng!"
        );
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

        try {
            nhatKyHoatDongService.ghiLog(
                getMaNguoiDungHienTai(),
                "Hủy phiếu mượn",
                "Hủy phiếu mượn mã: " + maPhieuMuon + " của độc giả " + phieuMuon.getNguoiDung().getHoTen()
            );
        } catch (Exception e) {}
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

        // Update reservation status when book is returned
        List<DatCho> reservations = datChoRepository.findBySach_MaSachAndTrangThaiOrderByThoiGianDatChoAsc(
                chiTietPhieuMuon.getCuonSach().getSach().getMaSach(), TrangThaiDatCho.DANG_CHO);
        if (!reservations.isEmpty()) {
            DatCho oldestReservation = reservations.get(0);
            oldestReservation.setHanGiuCho(LocalDateTime.now().plusDays(1));
            datChoRepository.save(oldestReservation);
        }

        if (now.isAfter(chiTietPhieuMuon.getHanTraHienTai())) {
            chiTietPhieuMuon.setTrangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon.DA_TRA_TRE);
            Long soNgayTre = ChronoUnit.DAYS.between((chiTietPhieuMuon.getHanTraHienTai()), now);
            double donGia = chiTietPhieuMuon.getDonGiaPhatApDung() != null
                    ? chiTietPhieuMuon.getDonGiaPhatApDung() : 0.0;
            double tienPhat = soNgayTre * donGia;
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

        try {
            nhatKyHoatDongService.ghiLog(
                getMaNguoiDungHienTai(),
                "Trả sách",
                "Nhận trả cuốn sách \"" + chiTietPhieuMuon.getCuonSach().getSach().getTenSach() + "\" từ độc giả " + chiTietPhieuMuon.getPhieuMuon().getNguoiDung().getHoTen() + ". Tình trạng lúc trả: " + request.getTinhTrangLucTra().name()
            );
        } catch (Exception e) {}

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
        if (LocalDateTime.now().isAfter(chiTietPhieuMuon.getHanTraHienTai())) {
            throw new LibraryException(ErrorCode.KHONG_THE_GIA_HAN_QUA_HAN);
        }

        if (chiTietPhieuMuon.getSoLanGiaHan() >= SO_LAN_GIA_HAN_TOI_DA) {
            throw new LibraryException(ErrorCode.VUOT_QUA_SO_LAN_GIA_HAN);
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
                .trangThai(TrangThaiGiaHan.DA_DUYET)
                .ngayThucHien(LocalDateTime.now())
                .build();

        lichSuGiaHanRepository.save(lichSuGiaHan);

        chiTietPhieuMuon.setHanTraHienTai(hanTraMoi);
        chiTietPhieuMuon.setSoLanGiaHan(chiTietPhieuMuon.getSoLanGiaHan() + 1);
        chiTietPhieuMuonRepository.save(chiTietPhieuMuon);
        return toGiaHanResponse(lichSuGiaHan);
    }

    @Override
    @Transactional
    public GiaHanResponse giaHanDocGia(UUID maChiTietPhieuMuon, UUID maNguoiDung) {
        ChiTietPhieuMuon chiTietPhieuMuon = chiTietPhieuMuonRepository.findById(maChiTietPhieuMuon)
                .orElseThrow(() -> new LibraryException(ErrorCode.CHI_TIET_PHIEU_MUON_KHONG_TON_TAI));

        if (!chiTietPhieuMuon.getPhieuMuon().getNguoiDung().getMaNguoiDung().equals(maNguoiDung)) {
            throw new LibraryException(ErrorCode.KHONG_CO_QUYEN);
        }

        if (chiTietPhieuMuon.getNgayTraThucTe() != null) {
            throw new LibraryException(ErrorCode.CHI_TIET_DA_TRA);
        }

        if (chiTietPhieuMuon.getSoLanGiaHan() >= SO_LAN_GIA_HAN_TOI_DA) {
            throw new LibraryException(ErrorCode.VUOT_QUA_SO_LAN_GIA_HAN);
        }

        if (chiTietPhieuMuon.getTrangThaiChiTietPhieuMuon() == TrangThaiChiTietPhieuMuon.QUA_HAN ||
            chiTietPhieuMuon.getHanTraHienTai().isBefore(LocalDateTime.now())) {
            throw new LibraryException(ErrorCode.KHONG_THE_GIA_HAN_QUA_HAN);
        }

        List<DatCho> reservations = datChoRepository.findBySach_MaSachAndTrangThaiOrderByThoiGianDatChoAsc(
            chiTietPhieuMuon.getCuonSach().getSach().getMaSach(), TrangThaiDatCho.DANG_CHO);
        if (!reservations.isEmpty()) {
            throw new LibraryException(ErrorCode.SACH_DA_CO_NGUOI_DAT_CHO);
        }

        LocalDateTime hanTraCu = chiTietPhieuMuon.getHanTraHienTai();
        LocalDateTime hanTraMoi = hanTraCu.plusDays(SO_NGAY_GIA_HAN);
        
        NguoiDung nguoiThucHien = nguoiDungService.getNguoiDungActive(maNguoiDung);

        LichSuGiaHan lichSuGiaHan = LichSuGiaHan.builder()
                .chiTietPhieuMuon(chiTietPhieuMuon)
                .hanTraCu(hanTraCu)
                .hanTraMoi(hanTraMoi)
                .lyDo("Độc giả tự gia hạn trực tuyến")
                .nguoiThucHien(nguoiThucHien)
                .trangThai(TrangThaiGiaHan.DA_DUYET)
                .ngayThucHien(LocalDateTime.now())
                .build();

        lichSuGiaHanRepository.save(lichSuGiaHan);

        chiTietPhieuMuon.setHanTraHienTai(hanTraMoi);
        chiTietPhieuMuon.setSoLanGiaHan(chiTietPhieuMuon.getSoLanGiaHan() + 1);
        chiTietPhieuMuonRepository.save(chiTietPhieuMuon);
        return toGiaHanResponse(lichSuGiaHan);
    }

    @Override
    @Transactional
    public GiaHanResponse yeuCauGiaHanDocGia(UUID maChiTietPhieuMuon, UUID maNguoiDung, int soNgayGiaHan) {
        ChiTietPhieuMuon chiTietPhieuMuon = chiTietPhieuMuonRepository.findById(maChiTietPhieuMuon)
                .orElseThrow(() -> new LibraryException(ErrorCode.CHI_TIET_PHIEU_MUON_KHONG_TON_TAI));

        if (!chiTietPhieuMuon.getPhieuMuon().getNguoiDung().getMaNguoiDung().equals(maNguoiDung)) {
            throw new LibraryException(ErrorCode.KHONG_CO_QUYEN);
        }

        if (chiTietPhieuMuon.getNgayTraThucTe() != null) {
            throw new LibraryException(ErrorCode.CHI_TIET_DA_TRA);
        }

        if (chiTietPhieuMuon.getSoLanGiaHan() >= SO_LAN_GIA_HAN_TOI_DA) {
            throw new LibraryException(ErrorCode.VUOT_QUA_SO_LAN_GIA_HAN);
        }

        if (chiTietPhieuMuon.getTrangThaiChiTietPhieuMuon() == TrangThaiChiTietPhieuMuon.QUA_HAN ||
            chiTietPhieuMuon.getHanTraHienTai().isBefore(LocalDateTime.now())) {
            throw new LibraryException(ErrorCode.KHONG_THE_GIA_HAN_QUA_HAN);
        }

        List<DatCho> reservations = datChoRepository.findBySach_MaSachAndTrangThaiOrderByThoiGianDatChoAsc(
            chiTietPhieuMuon.getCuonSach().getSach().getMaSach(), TrangThaiDatCho.DANG_CHO);
        if (!reservations.isEmpty()) {
            throw new LibraryException(ErrorCode.SACH_DA_CO_NGUOI_DAT_CHO);
        }

        // Check if there is already a pending renewal request for this copy
        List<LichSuGiaHan> pendingRequests = lichSuGiaHanRepository.findByChiTietPhieuMuon_MaChiTietPhieuMuonOrderByNgayTaoDesc(maChiTietPhieuMuon)
                .stream().filter(ls -> ls.getTrangThai() == TrangThaiGiaHan.CHO_DUYET).toList();
        if (!pendingRequests.isEmpty()) {
            throw new LibraryException(ErrorCode.INVALID_INPUT);
        }

        LocalDateTime hanTraCu = chiTietPhieuMuon.getHanTraHienTai();
        LocalDateTime hanTraMoi = hanTraCu.plusDays(soNgayGiaHan);
        
        NguoiDung nguoiThucHien = nguoiDungService.getNguoiDungActive(maNguoiDung);

        LichSuGiaHan lichSuGiaHan = LichSuGiaHan.builder()
                .chiTietPhieuMuon(chiTietPhieuMuon)
                .hanTraCu(hanTraCu)
                .hanTraMoi(hanTraMoi)
                .lyDo("Độc giả yêu cầu gia hạn trực tuyến (" + soNgayGiaHan + " ngày)")
                .nguoiThucHien(nguoiThucHien)
                .trangThai(TrangThaiGiaHan.CHO_DUYET)
                .ngayThucHien(LocalDateTime.now())
                .build();

        LichSuGiaHan saved = lichSuGiaHanRepository.save(lichSuGiaHan);

        try {
            thongBaoService.taoThongBaoChoAdmin(
                "Yêu cầu gia hạn mượn sách",
                "Độc giả " + nguoiThucHien.getHoTen() + " yêu cầu gia hạn cuốn sách \"" + chiTietPhieuMuon.getCuonSach().getSach().getTenSach() + "\".",
                LoaiThongBao.HE_THONG
            );
        } catch (Exception e) {
            // catch warning and avoid transaction rollback
        }

        try {
            nhatKyHoatDongService.ghiLog(
                nguoiThucHien.getMaNguoiDung(),
                "Yêu cầu gia hạn",
                "Độc giả " + nguoiThucHien.getHoTen() + " gửi yêu cầu gia hạn cuốn sách \"" + chiTietPhieuMuon.getCuonSach().getSach().getTenSach() + "\" thêm " + soNgayGiaHan + " ngày."
            );
        } catch (Exception e) {}

        return toGiaHanResponse(saved);
    }

    @Override
    @Transactional
    public GiaHanResponse duyetYeuCauGiaHan(UUID maLichSuGiaHan, UUID maAdmin, boolean dongY) {
        LichSuGiaHan ls = lichSuGiaHanRepository.findById(maLichSuGiaHan)
                .orElseThrow(() -> new LibraryException(ErrorCode.PHIEU_MUON_KHONG_TON_TAI));

        if (ls.getTrangThai() != TrangThaiGiaHan.CHO_DUYET) {
            throw new LibraryException(ErrorCode.INVALID_INPUT);
        }

        NguoiDung admin = nguoiDungService.getNguoiDungActive(maAdmin);

        if (dongY) {
            ls.setTrangThai(TrangThaiGiaHan.DA_DUYET);
            ls.setNguoiThucHien(admin);
            ls.setNgayThucHien(LocalDateTime.now());
            
            ChiTietPhieuMuon chiTiet = ls.getChiTietPhieuMuon();
            if (chiTiet.getSoLanGiaHan() >= SO_LAN_GIA_HAN_TOI_DA) {
                throw new LibraryException(ErrorCode.VUOT_QUA_SO_LAN_GIA_HAN);
            }
            chiTiet.setHanTraHienTai(ls.getHanTraMoi());
            chiTiet.setSoLanGiaHan(chiTiet.getSoLanGiaHan() + 1);
            chiTietPhieuMuonRepository.save(chiTiet);
        } else {
            ls.setTrangThai(TrangThaiGiaHan.TU_CHOI);
            ls.setNguoiThucHien(admin);
            ls.setNgayThucHien(LocalDateTime.now());
        }

        LichSuGiaHan saved = lichSuGiaHanRepository.save(ls);

        try {
            String title = dongY ? "Yêu cầu gia hạn được duyệt" : "Yêu cầu gia hạn bị từ chối";
            String msg = dongY ? 
                "Yêu cầu gia hạn cuốn sách \"" + ls.getChiTietPhieuMuon().getCuonSach().getSach().getTenSach() + "\" của bạn đã được phê duyệt." :
                "Yêu cầu gia hạn cuốn sách \"" + ls.getChiTietPhieuMuon().getCuonSach().getSach().getTenSach() + "\" của bạn đã bị từ chối.";
            thongBaoService.taoThongBao(
                ls.getChiTietPhieuMuon().getPhieuMuon().getNguoiDung().getMaNguoiDung(),
                title,
                msg,
                dongY ? LoaiThongBao.HE_THONG : LoaiThongBao.CANH_BAO
            );
        } catch (Exception e) {
            // catch warning and avoid transaction rollback
        }

        try {
            String act = dongY ? "Duyệt gia hạn" : "Từ chối gia hạn";
            String detail = (dongY ? "Phê duyệt đồng ý" : "Từ chối") + " gia hạn cuốn sách \"" + ls.getChiTietPhieuMuon().getCuonSach().getSach().getTenSach() + "\" cho độc giả " + ls.getChiTietPhieuMuon().getPhieuMuon().getNguoiDung().getHoTen() + ".";
            nhatKyHoatDongService.ghiLog(
                maAdmin,
                act,
                detail
            );
        } catch (Exception e) {}

        return toGiaHanResponse(saved);
    }

    @Override
    public List<GiaHanResponse> getDanhSachYeuCauGiaHan(TrangThaiGiaHan trangThai) {
        List<LichSuGiaHan> list;
        if (trangThai == null) {
            list = lichSuGiaHanRepository.findAllByOrderByNgayTaoDesc();
        } else {
            list = lichSuGiaHanRepository.findByTrangThaiOrderByNgayTaoDesc(trangThai);
        }
        return list.stream().map(this::toGiaHanResponse).collect(Collectors.toList());
    }

    @Override
    public List<GiaHanResponse> getLichSuGiaHanByChiTiet(UUID maChiTietPhieuMuon) {
        return lichSuGiaHanRepository.findByChiTietPhieuMuon_MaChiTietPhieuMuonOrderByNgayTaoDesc(maChiTietPhieuMuon)
                .stream().map(this::toGiaHanResponse).collect(Collectors.toList());
    }

    @Override
    public Page<PhieuMuonResponse> getAllPhieuMuon(Pageable pageable) {
        return phieuMuonRepository.findAll(pageable).map(this::toPhieuMuonResponse);
    }

    @Override
    public PhieuMuonResponse getPhieuMuonById(UUID maPhieuMuon) {
        return phieuMuonRepository.findById(maPhieuMuon)
                .map(this::toPhieuMuonResponse)
                .orElseThrow(() -> new LibraryException(ErrorCode.PHIEU_MUON_KHONG_TON_TAI));
    }

    @Override
    public Page<PhieuMuonResponse> getPhieuMuonByNguoiDung(UUID maNguoiDung, Pageable pageable) {
        return phieuMuonRepository.findByNguoiDung_MaNguoiDungOrderByNgayMuonDesc(maNguoiDung, pageable)
                .map(this::toPhieuMuonResponse);
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

        double giaTien = chiTietPhieuMuon.getCuonSach().getSach().getGiaTien() != null 
                ? chiTietPhieuMuon.getCuonSach().getSach().getGiaTien() : 0.0;
        double tienPhat = giaTien * 1.5;
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
                        .tenSach(ct.getCuonSach().getSach().getTenSach())
                        .hanTraBanDau(ct.getHanTraBanDau())
                        .hanTraHienTai(ct.getHanTraHienTai())
                        .ngayTraThucTe(ct.getNgayTraThucTe())
                        .tinhTrangLucMuon(ct.getTinhTrangLucMuon())
                        .tinhTrangLucTra(ct.getTinhTrangLucTra())
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
                .tenDocGia(nguoiDung.getHoTen())
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
                .tenSach(ct.getCuonSach().getSach().getTenSach())
                .hanTraBanDau(ct.getHanTraBanDau())
                .hanTraHienTai(ct.getHanTraHienTai())
                .ngayTraThucTe(ct.getNgayTraThucTe())
                .tinhTrangLucMuon(ct.getTinhTrangLucMuon())
                .tinhTrangLucTra(ct.getTinhTrangLucTra())
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
        ChiTietPhieuMuon ct = ls.getChiTietPhieuMuon();
        String tenSach = ct.getCuonSach() != null && ct.getCuonSach().getSach() != null ? ct.getCuonSach().getSach().getTenSach() : "N/A";
        
        NguoiDung docGia = ct.getPhieuMuon() != null ? ct.getPhieuMuon().getNguoiDung() : null;
        String tenDocGia = docGia != null ? docGia.getHoDem() + " " + docGia.getTen() : "N/A";
        String emailDocGia = docGia != null ? docGia.getEmail() : "N/A";

        return GiaHanResponse.builder()
                .maLichSuGiaHan(ls.getMaLichSuGiaHan())
                .maChiTietPhieuMuon(ct.getMaChiTietPhieuMuon())
                .hanTraCu(ls.getHanTraCu())
                .hanTraMoi(ls.getHanTraMoi())
                .lyDo(ls.getLyDo())
                .tenNguoiThucHien(ls.getNguoiThucHien() != null ? ls.getNguoiThucHien().getHoTen() : null)
                .trangThai(ls.getTrangThai())
                .ngayTao(ls.getNgayTao())
                .tenSach(tenSach)
                .tenDocGia(tenDocGia)
                .emailDocGia(emailDocGia)
                .build();
    }

    /**
     * Retrieves a CuonSach by barcode and validates it is SAN_SANG.
     * Centralises the availability check \u2014 mirrors getCuonSachAvailable(maSach) for barcode lookups,
     * avoiding inline duplication of the TrangThaiCuonSach.SAN_SANG check in createPhieuMuon.
     */
    private CuonSach getCuonSachAvailableByMaVach(String maVach) {
        CuonSach cuonSach = cuonSachService.getCuonSachByMaVach(maVach);
        if (cuonSach.getTrangThai() != TrangThaiCuonSach.SAN_SANG) {
            throw new LibraryException(ErrorCode.CUON_SACH_KHONG_SAN_SANG);
        }
        return cuonSach;
    }

    private UUID getMaNguoiDungHienTai() {
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof com.hcmunre.library.security.CustomUserDetails) {
                return ((com.hcmunre.library.security.CustomUserDetails) auth.getPrincipal()).getNguoiDung().getMaNguoiDung();
            }
        } catch (Exception e) {}
        return null;
    }

}
