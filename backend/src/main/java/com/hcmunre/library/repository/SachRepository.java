package com.hcmunre.library.repository;

import com.hcmunre.library.entity.Sach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SachRepository extends JpaRepository<Sach, Long> {
    // Lấy sách theo mã ISBN
    List<Sach> findByMaIsbn(String maIsbn);

    @Query("SELECT DISTINCT s FROM Sach s " +
           "LEFT JOIN s.danhSachTheLoai tl " +
           "LEFT JOIN s.danhSachTacGia tg " +
           "WHERE (CAST(:keyword AS string) IS NULL OR LOWER(s.tenSach) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))) " +
           "AND (:maTheLoai IS NULL OR tl.maTheLoai = :maTheLoai) " +
           "AND (:maTacGia IS NULL OR tg.maTacGia = :maTacGia) " +
           "AND (:maNhaXuatBan IS NULL OR s.nhaXuatBan.maNhaXuatBan = :maNhaXuatBan)")
    Page<Sach> searchAndFilter(
            @Param("keyword") String keyword,
            @Param("maTheLoai") Long maTheLoai,
            @Param("maTacGia") Long maTacGia,
            @Param("maNhaXuatBan") Long maNhaXuatBan,
            Pageable pageable);
}
