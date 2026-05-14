package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.SachRequest;
import com.hcmunre.library.dto.response.SachResponse;
import com.hcmunre.library.dto.response.NhaXuatBanResponse;
import com.hcmunre.library.dto.response.TacGiaResponse;
import com.hcmunre.library.dto.response.TheLoaiResponse;
import com.hcmunre.library.entity.NhaXuatBan;
import com.hcmunre.library.entity.Sach;
import com.hcmunre.library.entity.TacGia;
import com.hcmunre.library.entity.TheLoai;
import com.hcmunre.library.entity.HinhAnhSach;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.NhaXuatBanRepository;
import com.hcmunre.library.repository.SachRepository;
import com.hcmunre.library.repository.TacGiaRepository;
import com.hcmunre.library.repository.TheLoaiRepository;
import com.hcmunre.library.repository.CuonSachRepository;
import com.hcmunre.library.service.SachService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SachServiceImplement implements SachService {

    private final SachRepository sachRepository;
    private final NhaXuatBanRepository nhaXuatBanRepository;
    private final TheLoaiRepository theLoaiRepository;
    private final TacGiaRepository tacGiaRepository;
    private final CuonSachRepository cuonSachRepository;

    @Override
    public Page<SachResponse> getAllSach(Pageable pageable) {
        return sachRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public Page<SachResponse> searchAndFilterSach(String keyword, Long maTheLoai, Long maTacGia, Long maNhaXuatBan,
            Pageable pageable) {
        return sachRepository.searchAndFilter(keyword, maTheLoai, maTacGia, maNhaXuatBan, pageable)
                .map(this::toResponse);
    }

    @Override
    public SachResponse getSachById(Long id) {
        return sachRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new LibraryException(ErrorCode.SACH_KHONG_TON_TAI));
    }

    @Override
    @Transactional
    public SachResponse createSach(SachRequest request) {
        if (!sachRepository.findByMaIsbn(request.getMaIsbn()).isEmpty()) {
            throw new LibraryException(ErrorCode.ISBN_DA_TON_TAI);
        }

        Sach sach = Sach.builder()
                .tenSach(request.getTenSach())
                .maIsbn(request.getMaIsbn())
                .namXuatBan(request.getNamXuatBan())
                .lanTaiBan(request.getLanTaiBan())
                .soTrang(request.getSoTrang())
                .moTa(request.getMoTa())
                .giaTien(request.getGiaTien())
                .donGiaPhatTheoNgay(request.getDonGiaPhatTheoNgay())
                .kichThuoc(request.getKichThuoc())
                .dichGia(request.getDichGia())
                .build();

        if (request.getMaNhaXuatBan() != null) {
            NhaXuatBan nxb = nhaXuatBanRepository.findById(request.getMaNhaXuatBan())
                    .orElseThrow(() -> new LibraryException(ErrorCode.NHA_XUAT_BAN_KHONG_TON_TAI));
            sach.setNhaXuatBan(nxb);
        }

        if (request.getMaTheLoais() != null && !request.getMaTheLoais().isEmpty()) {
            List<TheLoai> theLoais = theLoaiRepository.findAllById(request.getMaTheLoais());
            sach.setDanhSachTheLoai(theLoais);
        }

        if (request.getMaTacGias() != null && !request.getMaTacGias().isEmpty()) {
            List<TacGia> tacGias = tacGiaRepository.findAllById(request.getMaTacGias());
            sach.setDanhSachTacGia(tacGias);
        }

        return toResponse(sachRepository.save(sach));
    }

    @Override
    @Transactional
    public SachResponse updateSach(Long id, SachRequest request) {
        Sach sach = sachRepository.findById(id)
                .orElseThrow(() -> new LibraryException(ErrorCode.SACH_KHONG_TON_TAI));

        if (request.getMaIsbn() != null) {
            List<Sach> existing = sachRepository.findByMaIsbn(request.getMaIsbn());
            boolean isbnTrungVoiSachKhac = existing.stream()
                    .anyMatch(s -> !s.getMaSach().equals(id));
            if (isbnTrungVoiSachKhac) {
                throw new LibraryException(ErrorCode.ISBN_DA_TON_TAI);
            }
        }

        sach.setTenSach(request.getTenSach());
        sach.setMaIsbn(request.getMaIsbn());
        sach.setNamXuatBan(request.getNamXuatBan());
        sach.setLanTaiBan(request.getLanTaiBan());
        sach.setSoTrang(request.getSoTrang());
        sach.setMoTa(request.getMoTa());
        sach.setGiaTien(request.getGiaTien());
        sach.setDonGiaPhatTheoNgay(request.getDonGiaPhatTheoNgay());
        sach.setKichThuoc(request.getKichThuoc());
        sach.setDichGia(request.getDichGia());

        if (request.getMaNhaXuatBan() != null) {
            NhaXuatBan nxb = nhaXuatBanRepository.findById(request.getMaNhaXuatBan())
                    .orElseThrow(() -> new LibraryException(ErrorCode.NHA_XUAT_BAN_KHONG_TON_TAI));
            sach.setNhaXuatBan(nxb);
        }

        if (request.getMaTheLoais() != null) {
            List<TheLoai> theLoais = theLoaiRepository.findAllById(request.getMaTheLoais());
            sach.setDanhSachTheLoai(theLoais);
        }

        if (request.getMaTacGias() != null) {
            List<TacGia> tacGias = tacGiaRepository.findAllById(request.getMaTacGias());
            sach.setDanhSachTacGia(tacGias);
        }

        return toResponse(sachRepository.save(sach));
    }

    @Override
    public void deleteSach(Long id) {
        Sach sach = sachRepository.findById(id).orElseThrow(
                () -> new LibraryException(ErrorCode.SACH_KHONG_TON_TAI));
        sach.setNgayXoa(LocalDateTime.now());
        sachRepository.save(sach);
    }

    private SachResponse toResponse(Sach sach) {
        NhaXuatBanResponse nxbResponse = null;
        if (sach.getNhaXuatBan() != null) {
            nxbResponse = NhaXuatBanResponse.builder()
                    .maNhaXuatBan(sach.getNhaXuatBan().getMaNhaXuatBan())
                    .tenNhaXuatBan(sach.getNhaXuatBan().getTenNhaXuatBan())
                    .diaChi(sach.getNhaXuatBan().getDiaChi())
                    .soDienThoai(sach.getNhaXuatBan().getSoDienThoai())
                    .email(sach.getNhaXuatBan().getEmail())
                    .build();
        }

        List<TacGiaResponse> tacGias = sach.getDanhSachTacGia() == null ? Collections.emptyList()
                : sach.getDanhSachTacGia().stream().map(tg -> TacGiaResponse.builder()
                        .maTacGia(tg.getMaTacGia())
                        .hoDem(tg.getHoDem())
                        .ten(tg.getTen())
                        .build()).collect(Collectors.toList());

        List<TheLoaiResponse> theLoais = sach.getDanhSachTheLoai() == null ? Collections.emptyList()
                : sach.getDanhSachTheLoai().stream().map(tl -> TheLoaiResponse.builder()
                        .maTheLoai(tl.getMaTheLoai())
                        .tenTheLoai(tl.getTenTheLoai())
                        .build()).collect(Collectors.toList());

        List<String> hinhAnhs = sach.getDanhSachHinhAnh() == null ? Collections.emptyList()
                : sach.getDanhSachHinhAnh().stream().map(HinhAnhSach::getDuongDan).collect(Collectors.toList());

        return SachResponse.builder()
                .maSach(sach.getMaSach())
                .tenSach(sach.getTenSach())
                .maIsbn(sach.getMaIsbn())
                .soTrang(sach.getSoTrang())
                .lanTaiBan(sach.getLanTaiBan())
                .namXuatBan(sach.getNamXuatBan())
                .kichThuoc(sach.getKichThuoc())
                .dichGia(sach.getDichGia())
                .giaTien(sach.getGiaTien())
                .donGiaPhatTheoNgay(sach.getDonGiaPhatTheoNgay())
                .moTa(sach.getMoTa())
                .nhaXuatBan(nxbResponse)
                .danhSachTacGia(tacGias)
                .danhSachTheLoai(theLoais)
                .danhSachHinhAnhUrl(hinhAnhs)
                .soLuongCoSan(cuonSachRepository.countBySach_MaSachAndTrangThai(
                        sach.getMaSach(), com.hcmunre.library.enums.TrangThaiCuonSach.SAN_SANG))
                .build();
    }
}
