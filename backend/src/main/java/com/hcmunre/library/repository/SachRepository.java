package com.hcmunre.library.repository;

import com.hcmunre.library.entity.Sach;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SachRepository extends JpaRepository<Sach, Long> {
    // Lấy danh sách tất cả các đầu sách
    List<Sach> findByTenSachContainingIgnoreCase(String keyword);

    // Lấy sách theo mã ISBN
    List<Sach> findByMaIsbn(String maIsbn);
}
