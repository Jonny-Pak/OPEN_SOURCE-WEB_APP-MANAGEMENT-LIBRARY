package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.SachRequest;
import com.hcmunre.library.entity.CuonSach;
import com.hcmunre.library.entity.NhaXuatBan;
import com.hcmunre.library.entity.Sach;
import com.hcmunre.library.entity.TacGia;
import com.hcmunre.library.entity.TheLoai;
import com.hcmunre.library.enums.TrangThaiCuonSach;
import com.hcmunre.library.exception.BusinessException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.repository.NhaXuatBanRepository;
import com.hcmunre.library.repository.SachRepository;
import com.hcmunre.library.repository.TacGiaRepository;
import com.hcmunre.library.repository.TheLoaiRepository;
import com.hcmunre.library.service.SachService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SachServiceImplement implements SachService {

    private final SachRepository sachRepository;
    private final NhaXuatBanRepository nhaXuatBanRepository;
    private final TheLoaiRepository theLoaiRepository;
    private final TacGiaRepository tacGiaRepository;

    // =============================================
    // === CRUD (Book module - bạn mình implement) ===
    // =============================================

    @Override
    public List<Sach> getAllSach() {
        return sachRepository.findAll();
    }

    @Override
    public List<Sach> searchSach(String keyword) {
        return sachRepository.findByTenSachContainingIgnoreCase(keyword);
    }

    @Override
    public Sach getSachById(Long id) {
        return sachRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SACH_KHONG_TON_TAI));
    }

    @Override
    @Transactional
    public Sach createSach(SachRequest request) {
        if (!sachRepository.findByMaIsbn(request.getMaIsbn()).isEmpty()) {
            throw new BusinessException(ErrorCode.ISBN_DA_TON_TAI);
        }

        Sach sach = Sach.builder()
                .tenSach(request.getTenSach())
                .maIsbn(request.getMaIsbn())
                .namXuatBan(request.getNamXuatBan())
                .lanTaiBan(request.getLanTaiBan())
                .soTrang(request.getSoTrang())
                .moTa(request.getMoTa())
                .donGiaPhatTheoNgay(request.getDonGiaPhatTheoNgay())
                .kichThuoc(request.getKichThuoc())
                .dichGia(request.getDichGia())
                .build();

        if (request.getMaNhaXuatBan() != null) {
            NhaXuatBan nxb = nhaXuatBanRepository.findById(request.getMaNhaXuatBan())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NHA_XUAT_BAN_KHONG_TON_TAI));
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

        return sachRepository.save(sach);
    }

    @Override
    @Transactional
    public Sach updateSach(Long id, SachRequest request) {
        Sach sach = sachRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SACH_KHONG_TON_TAI));

        if (request.getMaIsbn() != null) {
            List<Sach> existing = sachRepository.findByMaIsbn(request.getMaIsbn());
            boolean isbnTrungVoiSachKhac = existing.stream()
                    .anyMatch(s -> !s.getMaSach().equals(id));
            if (isbnTrungVoiSachKhac) {
                throw new BusinessException(ErrorCode.ISBN_DA_TON_TAI);
            }
        }

        sach.setTenSach(request.getTenSach());
        sach.setMaIsbn(request.getMaIsbn());
        sach.setNamXuatBan(request.getNamXuatBan());
        sach.setLanTaiBan(request.getLanTaiBan());
        sach.setSoTrang(request.getSoTrang());
        sach.setMoTa(request.getMoTa());
        sach.setDonGiaPhatTheoNgay(request.getDonGiaPhatTheoNgay());
        sach.setKichThuoc(request.getKichThuoc());
        sach.setDichGia(request.getDichGia());

        if (request.getMaNhaXuatBan() != null) {
            NhaXuatBan nxb = nhaXuatBanRepository.findById(request.getMaNhaXuatBan())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NHA_XUAT_BAN_KHONG_TON_TAI));
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

        return sachRepository.save(sach);
    }

    @Override
    public void deleteSach(Long id) {
        if (!sachRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.SACH_KHONG_TON_TAI);
        }
        sachRepository.deleteById(id);
    }

    // =============================================
    // === Borrowing System (stub - chờ implement) ===
    // =============================================

    @Override
    public CuonSach getCuonSachAvailable(Long maSach) {
        // TODO: implement - tìm cuốn sách có trạng thái SẴN SÀNG
        return null;
    }

    @Override
    public void updateTrangThaiCuonSach(Long maCuonSach, TrangThaiCuonSach trangThai) {
        // TODO: implement - cập nhật trạng thái cuốn sách (ĐANG_MƯỢN, SẴN_SÀNG, etc.)
    }
}
