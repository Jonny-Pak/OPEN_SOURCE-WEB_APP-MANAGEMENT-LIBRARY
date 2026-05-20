package com.hcmunre.library.repository;

import com.hcmunre.library.entity.NhatKyHoatDong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NhatKyHoatDongRepository extends JpaRepository<NhatKyHoatDong, UUID> {
    
    Page<NhatKyHoatDong> findAllByOrderByThoiGianDesc(Pageable pageable);

    @Query(value = """
            SELECT * FROM nhat_ky_hoat_dong n
            WHERE (CAST(:keyword AS TEXT) IS NULL
                   OR LOWER(n.ho_ten)    LIKE LOWER('%' || CAST(:keyword AS TEXT) || '%')
                   OR LOWER(n.hanh_dong) LIKE LOWER('%' || CAST(:keyword AS TEXT) || '%')
                   OR LOWER(n.chi_tiet)  LIKE LOWER('%' || CAST(:keyword AS TEXT) || '%'))
              AND (CAST(:vaiTro AS TEXT) IS NULL OR n.vai_tro = CAST(:vaiTro AS TEXT))
            ORDER BY n.thoi_gian DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM nhat_ky_hoat_dong n
            WHERE (CAST(:keyword AS TEXT) IS NULL
                   OR LOWER(n.ho_ten)    LIKE LOWER('%' || CAST(:keyword AS TEXT) || '%')
                   OR LOWER(n.hanh_dong) LIKE LOWER('%' || CAST(:keyword AS TEXT) || '%')
                   OR LOWER(n.chi_tiet)  LIKE LOWER('%' || CAST(:keyword AS TEXT) || '%'))
              AND (CAST(:vaiTro AS TEXT) IS NULL OR n.vai_tro = CAST(:vaiTro AS TEXT))
            """,
            nativeQuery = true)
    Page<NhatKyHoatDong> searchLogs(
            @Param("keyword") String keyword,
            @Param("vaiTro") String vaiTro,
            Pageable pageable);
}
