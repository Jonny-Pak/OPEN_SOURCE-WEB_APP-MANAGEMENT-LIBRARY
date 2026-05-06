package com.hcmunre.library.repository;

import com.hcmunre.library.entity.NhaXuatBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhaXuatBanRepository extends JpaRepository<NhaXuatBan, Long> {
    // Tìm kiếm nhà xuất bản theo tên (không phân biệt hoa thường)
    List<NhaXuatBan> findByTenNhaXuatBanContainingIgnoreCase(String keyword);
    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);
    // Kiểm tra email đã tồn tại chưa (trừ nhà xuất bản đang update)
    boolean existsByEmailAndMaNhaXuatBanNot(String email, Long maNhaXuatBan);
    // Kiểm tra SĐT đã tồn tại chưa
    boolean existsBySoDienThoai(String soDienThoai);
    // Kiểm tra SĐT đã tồn tại chưa (trừ nhà xuất bản đang update)
    boolean existsBySoDienThoaiAndMaNhaXuatBanNot(String soDienThoai, Long maNhaXuatBan);
}
