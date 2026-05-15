package com.hcmunre.library.repository;

import com.hcmunre.library.entity.SachYeuThich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SachYeuThichRepository extends JpaRepository<SachYeuThich, Long> {
    List<SachYeuThich> findByNguoiDung_MaNguoiDung(UUID maNguoiDung);
    boolean existsByNguoiDung_MaNguoiDungAndSach_MaSach(UUID maNguoiDung, Long maSach);
    void deleteByNguoiDung_MaNguoiDungAndSach_MaSach(UUID maNguoiDung, Long maSach);
}
