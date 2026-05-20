package com.hcmunre.library.repository;

import com.hcmunre.library.entity.Sach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SachRepository extends JpaRepository<Sach, Long> {
    // Lấy sách theo mã ISBN
    Optional<Sach> findByMaIsbn(String maIsbn);

    // Tải sách kèm danh sách hình ảnh bằng JOIN FETCH để tránh N+1
    @EntityGraph(attributePaths = {"danhSachHinhAnh", "nhaXuatBan"})
    Optional<Sach> findWithImagesByMaSach(Long maSach);

    @Query(
        value = "SELECT DISTINCT s FROM Sach s " +
                "LEFT JOIN s.danhSachTheLoai tl " +
                "LEFT JOIN s.danhSachTacGia tg " +
                "WHERE (CAST(:keyword AS string) IS NULL OR LOWER(s.tenSach) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))) " +
                "AND (:maTheLoai IS NULL OR tl.maTheLoai = :maTheLoai) " +
                "AND (:maTacGia IS NULL OR tg.maTacGia = :maTacGia) " +
                "AND (:maNhaXuatBan IS NULL OR s.nhaXuatBan.maNhaXuatBan = :maNhaXuatBan)",
        // countQuery riêng để tránh lỗi Hibernate HHH90003004 với DISTINCT + pagination
        countQuery = "SELECT COUNT(DISTINCT s) FROM Sach s " +
                     "LEFT JOIN s.danhSachTheLoai tl " +
                     "LEFT JOIN s.danhSachTacGia tg " +
                     "WHERE (CAST(:keyword AS string) IS NULL OR LOWER(s.tenSach) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))) " +
                     "AND (:maTheLoai IS NULL OR tl.maTheLoai = :maTheLoai) " +
                     "AND (:maTacGia IS NULL OR tg.maTacGia = :maTacGia) " +
                     "AND (:maNhaXuatBan IS NULL OR s.nhaXuatBan.maNhaXuatBan = :maNhaXuatBan)"
    )
    Page<Sach> searchAndFilter(
            @Param("keyword") String keyword,
            @Param("maTheLoai") Long maTheLoai,
            @Param("maTacGia") Long maTacGia,
            @Param("maNhaXuatBan") Long maNhaXuatBan,
            Pageable pageable);
}
