package com.hcmunre.library.repository;

import com.hcmunre.library.entity.PhieuPhat;
import com.hcmunre.library.enums.TrangThaiThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PhieuPhatRepository extends JpaRepository<PhieuPhat, UUID> {
    List<PhieuPhat> findByChiTietPhieuMuon_MaChiTietPhieuMuon(UUID maChiTietPhieuMuon);
    boolean existsByChiTietPhieuMuon_PhieuMuon_NguoiDung_MaNguoiDungAndTrangThaiThanhToan(
            UUID maNguoiDung, TrangThaiThanhToan trangThai);
}
