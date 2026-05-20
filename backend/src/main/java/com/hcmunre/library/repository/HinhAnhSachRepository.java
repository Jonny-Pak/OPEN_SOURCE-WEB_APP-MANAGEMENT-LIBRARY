package com.hcmunre.library.repository;

import com.hcmunre.library.entity.HinhAnhSach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhAnhSachRepository extends JpaRepository<HinhAnhSach, Long> {
    // Tìm kiếm các hình ảnh thuộc về một đầu sách cụ thể
    List<HinhAnhSach> findBySach_MaSach(Long maSach);
}
