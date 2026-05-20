package com.hcmunre.library.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SachResponse {
    private Long maSach;
    private String tenSach;
    private String maIsbn;
    private Integer soTrang;
    private Integer lanTaiBan;
    private Integer namXuatBan;
    private String kichThuoc;
    private String dichGia;
    private Double giaTien;
    private Double donGiaPhatTheoNgay;
    private String moTa;
    
    private NhaXuatBanResponse nhaXuatBan;
    private List<TacGiaResponse> danhSachTacGia;
    private List<TheLoaiResponse> danhSachTheLoai;
    private List<HinhAnhSachResponse> danhSachHinhAnh;
    private Long soLuongCoSan;
    private Long soLuongKho;
    private Long tongSoLuong;
}
