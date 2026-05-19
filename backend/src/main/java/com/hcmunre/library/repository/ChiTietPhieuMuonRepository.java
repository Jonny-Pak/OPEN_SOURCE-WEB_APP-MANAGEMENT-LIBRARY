package com.hcmunre.library.repository;

import com.hcmunre.library.entity.ChiTietPhieuMuon;
import com.hcmunre.library.enums.TrangThaiChiTietPhieuMuon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChiTietPhieuMuonRepository extends JpaRepository<ChiTietPhieuMuon, UUID> {
    boolean existsByPhieuMuon_NguoiDung_MaNguoiDungAndTrangThaiChiTietPhieuMuon(UUID maNguoiDung, TrangThaiChiTietPhieuMuon trangThaiChiTietPhieuMuon);
    int countByPhieuMuon_NguoiDung_MaNguoiDungAndTrangThaiChiTietPhieuMuonIn(UUID maNguoiDung, List<TrangThaiChiTietPhieuMuon> trangThaiChiTietPhieuMuonList);
    List<ChiTietPhieuMuon> findByTrangThaiChiTietPhieuMuonAndHanTraHienTaiBefore(
            TrangThaiChiTietPhieuMuon trangThai,
            LocalDateTime now
    );
    long countByTrangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon trangThai);
    long countByTrangThaiChiTietPhieuMuonIn(List<TrangThaiChiTietPhieuMuon> trangThais);

    /** Tìm các chi tiết đang mượn mà hạn trả nằm trong khoảng từ 'from' đến 'to' (dùng để nhắc trước 1 ngày) */
    List<ChiTietPhieuMuon> findByTrangThaiChiTietPhieuMuonAndHanTraHienTaiBetween(
            TrangThaiChiTietPhieuMuon trangThai,
            LocalDateTime from,
            LocalDateTime to
    );
}
