package com.hcmunre.library.repository;

import com.hcmunre.library.entity.PhieuMuon;
import com.hcmunre.library.entity.PhieuPhat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhieuMuonRepository extends JpaRepository<PhieuMuon, UUID> {
    List<PhieuMuon> findByNguoiDung_MaNguoiDungOrderByNgayMuonDesc(UUID maNguoiDung);

    List<PhieuPhat> findByDanhSachChiTietPhieuMuon_PhieuMuon_NguoiDung_MaNguoiDungOrderByNgayTaoDesc(UUID maNguoiDung);
}
