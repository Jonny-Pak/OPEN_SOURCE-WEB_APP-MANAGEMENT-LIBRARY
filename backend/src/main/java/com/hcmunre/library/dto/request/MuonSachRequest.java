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

    @NotEmpty(message = "Vui lòng chọn ít nhất một đầu sách")
    private List<Long> danhSachMaSach;
}
