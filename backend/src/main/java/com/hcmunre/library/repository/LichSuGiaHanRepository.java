package com.hcmunre.library.repository;

import com.hcmunre.library.entity.LichSuGiaHan;
import com.hcmunre.library.enums.TrangThaiGiaHan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LichSuGiaHanRepository extends JpaRepository<LichSuGiaHan, UUID> {
    List<LichSuGiaHan> findByChiTietPhieuMuon_MaChiTietPhieuMuonOrderByNgayTaoDesc(UUID maChiTiethieuMuon);
    List<LichSuGiaHan> findByTrangThaiOrderByNgayTaoDesc(TrangThaiGiaHan trangThai);
    List<LichSuGiaHan> findAllByOrderByNgayTaoDesc();
}
