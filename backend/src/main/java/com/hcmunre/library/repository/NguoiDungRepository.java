package com.hcmunre.library.repository;

import com.hcmunre.library.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository thao tác với bảng nguoi_dung trong database.
 * Cung cấp các phương thức truy vấn người dùng theo email,
 * kiểm tra trùng lặp email để phục vụ đăng nhập và đăng ký.
 */
@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, UUID> {

    /**
     * Tìm người dùng theo email.
     * Dùng trong quá trình đăng nhập (loadUserByUsername).
     *
     * @param email email cần tìm
     * @return Optional chứa NguoiDung nếu tìm thấy
     */
    Optional<NguoiDung> findByEmail(String email);

    /**
     * Kiểm tra email đã tồn tại trong hệ thống chưa.
     * Dùng khi đăng ký tài khoản mới để tránh trùng lặp.
     *
     * @param email email cần kiểm tra
     * @return true nếu email đã tồn tại
     */
    boolean existsByEmail(String email);

    boolean existsBySoDienThoai(String soDienThoai);

    boolean existsByCccd(String cccd);
    
    org.springframework.data.domain.Page<NguoiDung> findByNgayXoaIsNull(org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<NguoiDung> findByNgayXoaIsNullAndTrangThai(
            com.hcmunre.library.enums.TrangThaiNguoiDung trangThai, 
            org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT n FROM NguoiDung n WHERE n.ngayXoa IS NULL AND (" +
            "LOWER(n.hoDem) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "n.soDienThoai LIKE CONCAT('%', :keyword, '%'))")
    org.springframework.data.domain.Page<NguoiDung> searchNguoiDung(
            @org.springframework.data.repository.query.Param("keyword") String keyword, 
            org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT n FROM NguoiDung n WHERE n.ngayXoa IS NULL AND n.trangThai = :trangThai AND (" +
            "LOWER(n.hoDem) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "n.soDienThoai LIKE CONCAT('%', :keyword, '%'))")
    org.springframework.data.domain.Page<NguoiDung> searchNguoiDungWithStatus(
            @org.springframework.data.repository.query.Param("keyword") String keyword, 
            @org.springframework.data.repository.query.Param("trangThai") com.hcmunre.library.enums.TrangThaiNguoiDung trangThai, 
            org.springframework.data.domain.Pageable pageable);
}
