package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.request.DatChoRequest;
import com.hcmunre.library.dto.request.MuonSachRequest;
import com.hcmunre.library.dto.response.DatChoResponse;
import com.hcmunre.library.entity.CuonSach;
import com.hcmunre.library.entity.DatCho;
import com.hcmunre.library.entity.NguoiDung;
import com.hcmunre.library.entity.Sach;
import com.hcmunre.library.enums.TrangThaiCuonSach;
import com.hcmunre.library.enums.TrangThaiDatCho;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.exception.LibraryException;
import com.hcmunre.library.repository.CuonSachRepository;
import com.hcmunre.library.repository.DatChoRepository;
import com.hcmunre.library.repository.SachRepository;
import com.hcmunre.library.service.DatChoService;
import com.hcmunre.library.service.NguoiDungService;
import com.hcmunre.library.service.PhieuMuonService;
import com.hcmunre.library.service.PhieuPhatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DatChoServiceImplement implements DatChoService {
    private final DatChoRepository datChoRepository;
    private final NguoiDungService nguoiDungService;
    private final SachRepository sachRepository;
    private final CuonSachRepository cuonSachRepository;
    private final PhieuPhatService phieuPhatService;
    private final PhieuMuonService phieuMuonService;

    @Override
    @Transactional
    public DatChoResponse createDatCho(DatChoRequest request) {
        NguoiDung nguoiDung = nguoiDungService.getNguoiDungActive(request.getMaNguoiDung());

        if(phieuPhatService.hasPhieuPhatUnpaid(request.getMaNguoiDung())){
            throw new LibraryException(ErrorCode.NGUOI_DUNG_CON_NO_PHAT);
        }

        if(phieuMuonService.checkCuonSachQuaHan(nguoiDung.getMaNguoiDung())){
            throw new LibraryException(ErrorCode.SACH_DANG_QUA_HAN);
        }

        Sach sach = sachRepository.findById(request.getMaSach()).orElseThrow(
                () -> new LibraryException(ErrorCode.SACH_KHONG_TON_TAI)
        );

        boolean alreadyReserved = datChoRepository.existsByNguoiDung_MaNguoiDungAndSach_MaSachAndTrangThai(
                request.getMaNguoiDung(), request.getMaSach(), TrangThaiDatCho.DANG_CHO);
        if (alreadyReserved) {
            throw new LibraryException(ErrorCode.DA_DAT_CHO);
        }

        int SO_DAT_CHO_TOI_DA = 3;
        long userActiveReservations = datChoRepository.countByNguoiDung_MaNguoiDungAndTrangThai(
                request.getMaNguoiDung(), TrangThaiDatCho.DANG_CHO);
        if (userActiveReservations >= SO_DAT_CHO_TOI_DA) {
            throw new LibraryException(ErrorCode.VUOT_QUA_GIOI_HAN_DAT_CHO);
        }

        List<CuonSach> availableBooks = cuonSachRepository.findBySach_MaSachAndTrangThai(request.getMaSach(), TrangThaiCuonSach.SAN_SANG);
        long activeReservationsCount = datChoRepository.countBySach_MaSachAndTrangThai(request.getMaSach(), TrangThaiDatCho.DANG_CHO);
        
        if (availableBooks.size() <= activeReservationsCount) {
            throw new LibraryException(ErrorCode.CUON_SACH_KHONG_SAN_SANG);
        }

        LocalDateTime now = LocalDateTime.now();

        DatCho datCho = DatCho.builder()
                .nguoiDung(nguoiDung)
                .sach(sach)
                .thoiGianDatCho(now)
                .hanGiuCho(now.plusDays(1))
                .trangThai(TrangThaiDatCho.DANG_CHO)
                .build();

        return toDatChoResponse(datChoRepository.save(datCho));
    }

    @Override
    @Transactional
    public void cancelDatCho(UUID maDatCho, String ghiChuHuy) {
        DatCho datCho = datChoRepository.findById(maDatCho).orElseThrow(
                () -> new LibraryException(ErrorCode.DAT_CHO_KHONG_TON_TAI));

        if(datCho.getTrangThai() != TrangThaiDatCho.DANG_CHO){
            throw new LibraryException(ErrorCode.DAT_CHO_DA_XU_LY);
        }

        datCho.setTrangThai(TrangThaiDatCho.DA_HUY);
        datCho.setGhiChuHuy(ghiChuHuy);
        datChoRepository.save(datCho);
    }

    @Override
    @Transactional
    public DatChoResponse duyetDatCho(UUID maDatCho) {
        DatCho datCho = datChoRepository.findById(maDatCho).orElseThrow(
                () -> new LibraryException(ErrorCode.DAT_CHO_KHONG_TON_TAI)
        );

        if (datCho.getTrangThai() != TrangThaiDatCho.DANG_CHO) {
            throw new LibraryException(ErrorCode.DAT_CHO_DA_XU_LY);
        }

        // Find available copy of the book
        List<CuonSach> availableBooks = cuonSachRepository.findBySach_MaSachAndTrangThai(
                datCho.getSach().getMaSach(), TrangThaiCuonSach.SAN_SANG);
        
        if (availableBooks.isEmpty()) {
            throw new LibraryException(ErrorCode.CUON_SACH_KHONG_SAN_SANG);
        }

        CuonSach cuonSach = availableBooks.get(0);

        // Construct loan request
        MuonSachRequest request = new MuonSachRequest();
        request.setMaNguoiDung(datCho.getNguoiDung().getMaNguoiDung());
        request.setDanhSachMaVach(List.of(cuonSach.getMaVach()));

        // Create the loan, which will automatically update the DatCho status to DA_NHAN_SACH
        phieuMuonService.createPhieuMuon(request);

        // Fetch the updated reservation to return
        DatCho updatedDatCho = datChoRepository.findById(maDatCho).orElseThrow(
                () -> new LibraryException(ErrorCode.DAT_CHO_KHONG_TON_TAI)
        );

        return toDatChoResponse(updatedDatCho);
    }



    @Override
    public List<DatChoResponse> getAllDatCho() {
        return datChoRepository.findAll().stream().map(this::toDatChoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DatChoResponse> getDatChoByNguoiDung(UUID maNguoiDung) {
        return datChoRepository.findByNguoiDung_MaNguoiDungOrderByThoiGianDatChoDesc(maNguoiDung)
                .stream().map(this::toDatChoResponse).collect(Collectors.toList());
    }

    private DatChoResponse toDatChoResponse(DatCho datCho){
        return DatChoResponse.builder()
                .maDatCho(datCho.getMaDatCho())
                .maSach(datCho.getSach().getMaSach())
                .tenSach(datCho.getSach().getTenSach())
                .maIsbn(datCho.getSach().getMaIsbn())
                .maNguoiDung(datCho.getNguoiDung().getMaNguoiDung())
                .hoDemNguoiDung(datCho.getNguoiDung().getHoDem())
                .tenNguoiDung(datCho.getNguoiDung().getTen())
                .emailNguoiDung(datCho.getNguoiDung().getEmail())
                .thoiGianDatCho(datCho.getThoiGianDatCho())
                .hanGiuCho(datCho.getHanGiuCho())
                .trangThaiDatCho(datCho.getTrangThai())
                .ghiChuHuy(datCho.getGhiChuHuy())
                .build();
    }

    @Override
    @Transactional
    public void updateStatusOnBookReturn(Long maSach) {
        List<DatCho> reservations = datChoRepository.findBySach_MaSachAndTrangThaiOrderByThoiGianDatChoAsc(maSach, TrangThaiDatCho.DANG_CHO);
        if (!reservations.isEmpty()) {
            DatCho oldestReservation = reservations.get(0);
            oldestReservation.setHanGiuCho(LocalDateTime.now().plusDays(1));
            datChoRepository.save(oldestReservation);
        }
    }
}
