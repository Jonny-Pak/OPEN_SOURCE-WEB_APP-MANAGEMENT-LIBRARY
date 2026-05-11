package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GiaHanRequest {
    @NotNull(message = "Mã chi tiết phiếu mượn không được để trống")
    private UUID maChiTietPhieuMuon;

    @NotNull(message = "Mã người thực hiện không được để trống")
    private UUID maNguoiThucHien;

    private String lyDo;
}
