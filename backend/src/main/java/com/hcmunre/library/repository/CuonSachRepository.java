package com.hcmunre.library.repository;

import com.hcmunre.library.entity.CuonSach;
import com.hcmunre.library.enums.TrangThaiCuonSach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuonSachRepository extends JpaRepository<CuonSach, Long> {
    // Tìm kiếm các bản sao thuộc về một đầu sách cụ thể
    List<CuonSach> findBySach_MaSach(Long maSach);

    // Tìm bản sao theo mã vạch
    CuonSach findByMaVach(String maVach);

    // Kiểm tra mã vạch đã tồn tại chưa (dùng khi tạo mới)
    boolean existsByMaVach(String maVach);

    // Kiểm tra mã vạch đã tồn tại chưa (trừ cuốn sách đang update)
    boolean existsByMaVachAndMaCuonSachNot(String maVach, Long maCuonSach);

    // Đếm số lượng cuốn đang sẵn sàng (SAN_SANG) của một đầu sách
    long countBySach_MaSachAndTrangThai(Long maSach, TrangThaiCuonSach trangThai);

    // Lấy danh sách cuốn theo đầu sách và trạng thái
    List<CuonSach> findBySach_MaSachAndTrangThai(Long maSach, TrangThaiCuonSach trangThai);
}
