package com.hcmunre.library.dto.request;

import com.hcmunre.library.enums.TinhTrangVatLy;
import com.hcmunre.library.enums.TrangThaiCuonSach;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CuonSachRequest {

    @NotBlank(message = "Mã vạch không được để trống")
    private String maVach;

    private String viTriKe;

    @NotNull(message = "Trạng thái cuốn sách không được để trống")
    private TrangThaiCuonSach trangThai;

    private TinhTrangVatLy tinhTrangVatLy;

    private String ghiChuBaoTri;

    @NotNull(message = "Phải chỉ định đầu sách (maSach) cho cuốn sách")
    private Long maSach;
}
