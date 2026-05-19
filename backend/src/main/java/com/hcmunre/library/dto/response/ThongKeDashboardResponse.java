package com.hcmunre.library.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThongKeDashboardResponse {
    private long soBanDangMuon;
    private long soSachQuaHan;
    private double tongTienPhat;
    private long tongNguoiDung;
}
