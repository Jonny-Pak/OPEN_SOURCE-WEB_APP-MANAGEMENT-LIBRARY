package com.hcmunre.library.repository;

import com.hcmunre.library.entity.CuonSach;
import com.hcmunre.library.enums.TrangThaiCuonSach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuonSachRepository extends JpaRepository<CuonSach, Long> {

    @Override
    @Query("SELECT c FROM CuonSach c JOIN FETCH c.sach")
    List<CuonSach> findAll();

    @Override
    @Query("SELECT c FROM CuonSach c JOIN FETCH c.sach WHERE c.maCuonSach = :id")
    Optional<CuonSach> findById(@Param("id") Long id);

    // Tìm kiếm các bản sao thuộc về một đầu sách cụ thể
    @Query("SELECT c FROM CuonSach c JOIN FETCH c.sach WHERE c.sach.maSach = :maSach")
    List<CuonSach> findBySach_MaSach(@Param("maSach") Long maSach);

    // Tìm bản sao theo mã vạch
    @Query("SELECT c FROM CuonSach c JOIN FETCH c.sach WHERE c.maVach = :maVach")
    CuonSach findByMaVach(@Param("maVach") String maVach);

    // Kiểm tra mã vạch đã tồn tại chưa (dùng khi tạo mới)
    boolean existsByMaVach(String maVach);

    // Kiểm tra mã vạch đã tồn tại chưa (trừ cuốn sách đang update)
    boolean existsByMaVachAndMaCuonSachNot(String maVach, Long maCuonSach);

    // Đếm số lượng cuốn đang sẵn sàng (SAN_SANG) của một đầu sách
    long countBySach_MaSachAndTrangThai(Long maSach, TrangThaiCuonSach trangThai);

    // Đếm tổng số lượng cuốn sách của một đầu sách
    long countBySach_MaSach(Long maSach);

    // Lấy danh sách cuốn theo đầu sách và trạng thái
    @Query("SELECT c FROM CuonSach c JOIN FETCH c.sach WHERE c.sach.maSach = :maSach AND c.trangThai = :trangThai")
    List<CuonSach> findBySach_MaSachAndTrangThai(@Param("maSach") Long maSach, @Param("trangThai") TrangThaiCuonSach trangThai);

    // Đếm tổng số cuốn sách theo trạng thái trên toàn hệ thống
    long countByTrangThai(TrangThaiCuonSach trangThai);
}
