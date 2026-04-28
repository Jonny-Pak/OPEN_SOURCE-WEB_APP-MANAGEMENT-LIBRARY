package com.hcmunre.library.repository;

import com.hcmunre.library.entity.TacGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacGiaRepository extends JpaRepository<TacGia, Long> {
    // Tìm kiếm tác giả theo tên (không phân biệt hoa thường)
    List<TacGia> findByTenContainingIgnoreCase(String keyword);
}
