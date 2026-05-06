package com.hcmunre.library.service.implement;

import com.hcmunre.library.dto.response.PhieuPhatResponse;
import com.hcmunre.library.entity.ChiTietPhieuMuon;
import com.hcmunre.library.entity.PhieuPhat;
import com.hcmunre.library.enums.TrangThaiThanhToan;
import com.hcmunre.library.exception.BusinessException;
import com.hcmunre.library.exception.ErrorCode;
import com.hcmunre.library.exception.ResourceNotFoundException;
import com.hcmunre.library.repository.ChiTietPhieuMuonRepository;
import com.hcmunre.library.repository.PhieuPhatRepository;
import com.hcmunre.library.service.PhieuPhatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhieuPhatServiceImplement implements PhieuPhatService {
    private final ChiTietPhieuMuonRepository chiTietPhieuMuonRepository;
    private final PhieuPhatRepository phieuPhatRepository;

    @Override
    public PhieuPhatResponse createPhieuPhat(UUID maChiTietPhieuMuon, Double tienPhat, String lyDoPhat) {
        ChiTietPhieuMuon chiTietPhieuMuon = chiTietPhieuMuonRepository.findById(maChiTietPhieuMuon).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCode.PHIEU_MUON_KHONG_TON_TAI));

        PhieuPhat phieuPhat = PhieuPhat.builder()
                .chiTietPhieuMuon(chiTietPhieuMuon)
                .soTienPhat(tienPhat)
                .lyDoPhat(lyDoPhat)
                .trangThaiThanhToan(TrangThaiThanhToan.CHUA_THANH_TOAN)
                .build();

        return toResponse(phieuPhat);
    }

    @Override
    public PhieuPhatResponse payPhieuPhat(UUID maPhieuPhat) {
        PhieuPhat phieuPhat = phieuPhatRepository.findById(maPhieuPhat).orElseThrow(()
                -> new ResourceNotFoundException(ErrorCode.PHIEU_PHAT_KHONG_TON_TAI));

        if(phieuPhat.getTrangThaiThanhToan() == TrangThaiThanhToan.DA_THANH_TOAN){
            throw new BusinessException(ErrorCode.PHIEU_PHAT_DA_THANH_TOAN);
        }

        phieuPhat.setTrangThaiThanhToan(TrangThaiThanhToan.DA_THANH_TOAN);
        phieuPhat.setNgayThanhToan(LocalDateTime.now());

        return toResponse(phieuPhat);
    }

    @Override
    public List<PhieuPhatResponse> getPhieuPhatByChiTiet(UUID maChiTietPhieuMuon) {
        return phieuPhatRepository.findByChiTietPhieuMuon_MaChiTietPhieuMuon(maChiTietPhieuMuon)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public boolean hasPhieuPhatUnpaid(UUID maNguoiDung) {
        return phieuPhatRepository.existsByChiTietPhieuMuon_PhieuMuon_NguoiDung_MaNguoiDungAndTrangThaiThanhToan(
                maNguoiDung, TrangThaiThanhToan.CHUA_THANH_TOAN
        );
    }

    private PhieuPhatResponse toResponse(PhieuPhat pp) {
        return PhieuPhatResponse.builder()
                .maPhieuPhat(pp.getMaPhieuPhat())
                .maChiTietPhieuMuon(pp.getChiTietPhieuMuon().getMaChiTietPhieuMuon())
                .soTienPhat(pp.getSoTienPhat())
                .lyDoPhat(pp.getLyDoPhat())
                .trangThaiThanhToan(pp.getTrangThaiThanhToan())
                .ngayThanhToan(pp.getNgayThanhToan())
                .ngayTao(pp.getNgayTao())
                .build();
    }

    // =============================================
    // === CRUD (Book module - chờ implement) ===
    // =============================================

    @Override
    public List<PhieuPhat> getAllPhieuPhat() {
        // TODO: implement
        return phieuPhatRepository.findAll();
    }

    @Override
    public PhieuPhat getPhieuPhatById(UUID id) {
        // TODO: implement
        return phieuPhatRepository.findById(id).orElse(null);
    }

    @Override
    public PhieuPhat createPhieuPhat(PhieuPhat phieuPhat) {
        // TODO: implement
        return phieuPhatRepository.save(phieuPhat);
    }

    @Override
    public PhieuPhat updatePhieuPhat(UUID id, PhieuPhat phieuPhat) {
        // TODO: implement
        return null;
    }

    @Override
    public void deletePhieuPhat(UUID id) {
        // TODO: implement
        phieuPhatRepository.deleteById(id);
    }
}
