package com.hcmunre.library.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaHanResponse {
    private UUID maLichSuGiaHan;
    private UUID maChiTietPhieuMuon;
    private String tenNguoiThucHien;
    private LocalDateTime hanTraCu;
    private LocalDateTime hanTraMoi;
    private String lyDo;
    private LocalDateTime ngayTao;
}
