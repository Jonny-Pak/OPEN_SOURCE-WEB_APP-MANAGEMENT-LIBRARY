package com.hcmunre.library.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MuonSachRequest {
    @NotNull(message = "Mã người dùng không được để trống")
    private UUID maNguoiDung;

    @NotEmpty(message = "Vui lòng quét ít nhất một cuốn sách")
    private List<String> danhSachMaVach;
}
