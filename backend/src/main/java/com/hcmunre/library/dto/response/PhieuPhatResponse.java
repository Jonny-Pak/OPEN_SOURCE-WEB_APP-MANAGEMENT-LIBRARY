package com.hcmunre.library.dto.response;

import com.hcmunre.library.enums.TrangThaiThanhToan;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuPhatResponse {
    private UUID maPhieuPhat;
    private UUID maChiTietPhieuMuon;
    private Double soTienPhat;
    private String lyDoPhat;
    private TrangThaiThanhToan trangThaiThanhToan;
    private LocalDateTime ngayThanhToan;
    private LocalDateTime ngayTao;

    private NguoiDungResponse nguoiDung;
    private UUID maPhieuMuon;
    private String lyDo;
    private String trangThai;

    // Thông tin cuốn sách bị phạt
    private String tenSach;
    private String maVachCuonSach;
}
