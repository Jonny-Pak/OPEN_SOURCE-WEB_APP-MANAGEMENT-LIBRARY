package com.hcmunre.library.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImportExcelResponse {
    private int tongSoDong;
    private int thanhCong;
    private int thatBai;
    private List<String> danhSachLoi;
}
