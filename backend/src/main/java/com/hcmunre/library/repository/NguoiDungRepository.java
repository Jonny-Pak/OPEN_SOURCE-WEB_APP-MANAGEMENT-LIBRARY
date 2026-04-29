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
}
