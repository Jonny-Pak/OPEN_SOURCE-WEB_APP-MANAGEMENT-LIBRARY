package com.hcmunre.library.repository;

import com.hcmunre.library.entity.DatCho;
import com.hcmunre.library.enums.TrangThaiDatCho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DatChoRepository extends JpaRepository<DatCho, UUID> {
    List<DatCho> findByNguoiDung_MaNguoiDungOrderByThoiGianDatChoDesc(UUID maNguoiDung);
    boolean existsByNguoiDung_MaNguoiDungAndSach_MaSachAndTrangThai(UUID maNguoiDung, Long maSach, TrangThaiDatCho trangThaiDatCho);
    List<DatCho> findByNguoiDung_MaNguoiDungAndSach_MaSachAndTrangThai(UUID maNguoiDung, Long maSach, TrangThaiDatCho trangThai);
    List<DatCho> findByTrangThaiAndHanGiuChoBefore(TrangThaiDatCho trangThai, LocalDateTime hanGiuCho);
    long countBySach_MaSachAndTrangThai(Long maSach, TrangThaiDatCho trangThai);
    long countByNguoiDung_MaNguoiDungAndTrangThai(UUID maNguoiDung, TrangThaiDatCho trangThai);
    List<DatCho> findBySach_MaSachAndTrangThaiOrderByThoiGianDatChoAsc(Long maSach, TrangThaiDatCho trangThai);
}
