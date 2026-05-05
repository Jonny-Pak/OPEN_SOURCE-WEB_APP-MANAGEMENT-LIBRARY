package com.hcmunre.library.repository;

import com.hcmunre.library.entity.TheLoai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheLoaiRepository extends JpaRepository<TheLoai, Long> {
    // Tìm kiếm thể loại theo tên (không phân biệt hoa thường)
    List<TheLoai> findByTenTheLoaiContainingIgnoreCase(String keyword);
}
