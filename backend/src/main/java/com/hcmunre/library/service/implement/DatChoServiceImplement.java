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
import com.hcmunre.library.service.ThongBaoService;
import com.hcmunre.library.enums.LoaiThongBao;
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
    private final ThongBaoService thongBaoService;
    private final com.hcmunre.library.service.NhatKyHoatDongService nhatKyHoatDongService;

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

        DatCho savedDatCho = datChoRepository.save(datCho);

        try {
            thongBaoService.taoThongBaoChoAdmin(
                "Yêu cầu đặt chỗ mới",
                "Độc giả " + nguoiDung.getHoTen() + " đã đặt chỗ sách \"" + sach.getTenSach() + "\". Vui lòng chuẩn bị và duyệt mượn.",
                LoaiThongBao.DAT_CHO
            );
        } catch (Exception e) {
            // catch warning and avoid transaction rollback
        }

        try {
            nhatKyHoatDongService.ghiLog(
                nguoiDung.getMaNguoiDung(),
                "Đặt chỗ sách",
                "Độc giả " + nguoiDung.getHoTen() + " đã yêu cầu đặt chỗ cuốn sách \"" + sach.getTenSach() + "\"."
            );
        } catch (Exception e) {
            // ignore
        }

        return toDatChoResponse(savedDatCho);
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

        try {
            thongBaoService.taoThongBao(
                datCho.getNguoiDung().getMaNguoiDung(),
                "Yêu cầu đặt chỗ bị từ chối/hủy",
                "Đơn đặt chỗ cuốn sách \"" + datCho.getSach().getTenSach() + "\" của bạn đã bị hủy. Lý do: " + ghiChuHuy,
                LoaiThongBao.CANH_BAO
            );
        } catch (Exception e) {
            // catch warning and avoid transaction rollback
        }

        try {
            nhatKyHoatDongService.ghiLog(
                getMaNguoiDungHienTai(),
                "Hủy đặt chỗ sách",
                "Hủy đơn đặt chỗ cuốn sách \"" + datCho.getSach().getTenSach() + "\" của độc giả " + datCho.getNguoiDung().getHoTen() + ". Lý do: " + ghiChuHuy
            );
        } catch (Exception e) {}
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

        try {
            thongBaoService.taoThongBao(
                datCho.getNguoiDung().getMaNguoiDung(),
                "Yêu cầu đặt chỗ được duyệt",
                "Đơn đặt chỗ cuốn sách \"" + datCho.getSach().getTenSach() + "\" của bạn đã được duyệt thành công! Bạn có thể nhận sách.",
                LoaiThongBao.HE_THONG
            );
        } catch (Exception e) {
            // catch warning and avoid transaction rollback
        }

        try {
            nhatKyHoatDongService.ghiLog(
                getMaNguoiDungHienTai(),
                "Duyệt đặt chỗ",
                "Phê duyệt đặt chỗ sách \"" + datCho.getSach().getTenSach() + "\" cho độc giả " + datCho.getNguoiDung().getHoTen() + "."
            );
        } catch (Exception e) {}

        return toDatChoResponse(updatedDatCho);
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
        Long maSach = null;
        String tenSach = "Đầu sách đã bị xóa";
        String maIsbn = "N/A";
        try {
            if (datCho.getSach() != null) {
                maSach = datCho.getSach().getMaSach();
                tenSach = datCho.getSach().getTenSach();
                maIsbn = datCho.getSach().getMaIsbn();
            }
        } catch (jakarta.persistence.EntityNotFoundException | org.hibernate.ObjectNotFoundException e) {
            // Gracefully handle deleted book
        }

        UUID maNguoiDung = null;
        String hoDemNguoiDung = "";
        String tenNguoiDung = "Độc giả đã bị xóa";
        String emailNguoiDung = "N/A";
        try {
            if (datCho.getNguoiDung() != null) {
                maNguoiDung = datCho.getNguoiDung().getMaNguoiDung();
                hoDemNguoiDung = datCho.getNguoiDung().getHoDem();
                tenNguoiDung = datCho.getNguoiDung().getTen();
                emailNguoiDung = datCho.getNguoiDung().getEmail();
            }
        } catch (jakarta.persistence.EntityNotFoundException | org.hibernate.ObjectNotFoundException e) {
            // Gracefully handle deleted user
        }

        return DatChoResponse.builder()
                .maDatCho(datCho.getMaDatCho())
                .maSach(maSach)
                .tenSach(tenSach)
                .maIsbn(maIsbn)
                .maNguoiDung(maNguoiDung)
                .hoDemNguoiDung(hoDemNguoiDung)
                .tenNguoiDung(tenNguoiDung)
                .emailNguoiDung(emailNguoiDung)
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
