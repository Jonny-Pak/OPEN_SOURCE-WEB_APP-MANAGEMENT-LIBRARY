package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class GiaHanRequest {
    @NotNull(message = "Mã chi tiết phiếu mượn không được ể trống")
    private UUID maChiTietPhieuMuon;
    private String lyDo;
}
