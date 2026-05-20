package com.hcmunre.library.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TheLoaiResponse {
    private Long maTheLoai;
    private String tenTheLoai;
    private String moTa;
}
