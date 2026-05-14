package com.hcmunre.library.repository;

import com.hcmunre.library.entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, UUID> {
    List<ThongBao> findByNguoiDung_MaNguoiDungOrderByNgayTaoDesc(UUID maNguoiDung);
    long countByNguoiDung_MaNguoiDungAndDaDocFalse(UUID maNguoiDung);
    List<ThongBao> findByNguoiDung_MaNguoiDungAndDaDocFalse(UUID maNguoiDung);
}
